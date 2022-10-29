# gulimall

## 日志记录

1. 2022.07.29 09:48 创建并初始化项目结构
2. 2022.07.30 15:13 
    - 引入renren-fast、renren-generator
    - 使用renren-generator逆向生成基本的增删改查代码。
    - 完成各个微服务模块配置，并通过测试（连接数据库获取数据）
3. 2022.07.31 11:45
   - 引入springboot alibaba
   - 使用nacos作为注册中心、配置中心
   - 使用spring cloud feign做请求处理
   - 修改spring boot、spring cloud版本适配本项目
4. 2022.10.22



## 查询首页分类数据的优化过程

### 原始方法

```java
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        // 1. 查出所有1级分类
        List<CategoryEntity> level1Categorys = getLevel1Categorys();
        // 2. 封装数据
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();//一级分类的id作为key
        }, v -> {
            // 2.1 根据1级分类id查询二级分类
            List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            // 2.2 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 2.3 根2级分类id查询3级分类
                    List<CategoryEntity> level3Catelog = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if(level3Catelog != null){
                        catelog3Vos = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName().toString());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3Vos);

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return collect;
    }
```

#### 优化一：抽取方法，减少数据库查询

```java
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parent_cid;//当前分类的父id为parent_cid
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

            String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");//从缓存中获取

            List<CategoryEntity> selectList = baseMapper.selectList(null);//查询所有分类

            // 1. 查出所有1级分类
            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
            // 2. 封装数据
            Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> {
                return k.getCatId().toString();//一级分类的id作为key
            }, v -> {
                // 2.1 根据1级分类id查询二级分类
                List<CategoryEntity> entities = getParent_cid(selectList, v.getCatId());
                // 2.2 封装上面的结果
                List<Catelog2Vo> catelog2Vos = null;
                if (entities != null) {
                    catelog2Vos = entities.stream().map(l2 -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                        // 2.3 根2级分类id查询3级分类
                        List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                        List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                        if(level3Catelog != null){
                            catelog3Vos = level3Catelog.stream().map(l3 -> {
                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName().toString());
                                return catelog3Vo;
                            }).collect(Collectors.toList());
                        }
                        catelog2Vo.setCatalog3List(catelog3Vos);

                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            
            return collect;
    }
```

### 优化二：使用redis缓存

```java
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
    }

    /**
     * 优化查询1：从数据库查询1次
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDB() {

            String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");//从缓存中获取

            List<CategoryEntity> selectList = baseMapper.selectList(null);//查询所有分类

            // 1. 查出所有1级分类
            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
            // 2. 封装数据
            Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> {
                ……
            }));

           String jsonString = JSON.toJSONString(collect);//将对象转换为json字符串，用于存入redis【之所以不采用对象序列化的方式，原因是转换为json后其他语言也可以使用】
           redisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);//给缓存设置数据[设置过期时间，解决穿透、雪崩问题]
            
            return collect;
    }
    
   @Override
   public Map<String, List<Catelog2Vo>> getCatalogJson() {
          String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");//从缓存中获取
   
           if(StringUtils.isEmpty(catalogJSON)) {
              Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithLocalLock();//redis中没有才去数据库中查询
              return catalogJsonFromDB;
           }
   
           Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});// TypeReference使用了protected修饰，所以这里使用了匿名内部类的方式
           return result;
        }
```

### 优化三：使用本地锁

```java
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithLocalLock() {
        synchronized (this){
            ……
            return collect;
        }
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
    }
```

### 优化四：使用分布式锁1

```java
private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 1. 占分布式锁，去redis占坑
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "111");
        if(lock){//加锁成功，执行业务
            Map<String, List<Catelog2Vo>> dataFromDB = getDataFromDB();
            redisTemplate.delete("lock");//释放锁
            return dataFromDB;
        }else {//加锁失败，执行重试
            // 可以设置休眠时间
            return getCatalogJsonFromDBWithRedisLock();//自旋锁
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");//从缓存中获取

        if(!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return result;
        }

        List<CategoryEntity> selectList = baseMapper.selectList(null);//查询所有分类

        // 1. 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2. 封装数据
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();//一级分类的id作为key
        }, v -> {
            // 2.1 根据1级分类id查询二级分类
            List<CategoryEntity> entities = getParent_cid(selectList, v.getCatId());
            // 2.2 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 2.3 根2级分类id查询3级分类
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if(level3Catelog != null){
                        catelog3Vos = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName().toString());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3Vos);

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        String jsonString = JSON.toJSONString(collect);//将对象转换为json字符串，用于存入redis【之所以不采用对象序列化的方式，原因是转换为json后其他语言也可以使用】
        redisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);//给缓存设置数据[设置过期时间，解决穿透、雪崩问题]

        return collect;
    }
    
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
        return result;
    }
```

### 优化五：使用分布式锁2

```java
private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
        return collect;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 1. 占分布式锁，去redis占坑
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "111");
        if(lock){//加锁成功，执行业务
            redisTemplate.expire("lock", 1, TimeUnit.DAYS);//设置过期时间
            Map<String, List<Catelog2Vo>> dataFromDB = getDataFromDB();
            redisTemplate.delete("lock");//释放锁
            return dataFromDB;
        }else {//加锁失败，执行重试
            // 可以设置休眠时间
            return getCatalogJsonFromDBWithRedisLock();//自旋锁
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        ……
        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
        return result;
    }
```

### 优化六：分布式锁3

```java
private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
        return collect;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 1. 占分布式锁，去redis占坑
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "111", 300, TimeUnit.SECONDS);//加锁并设置过期时间
        if(lock){//加锁成功，执行业务
            Map<String, List<Catelog2Vo>> dataFromDB = getDataFromDB();
            redisTemplate.delete("lock");//释放锁
            return dataFromDB;
        }else {//加锁失败，执行重试
            // 可以设置休眠时间
            return getCatalogJsonFromDBWithRedisLock();//自旋锁
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        ……
        return collect;
    }
    
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
        return result;
    }
```

### 优化七：分布式锁4

```java
 private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
        return collect;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 1. 占分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);//加锁并设置过期时间
        if(lock){//加锁成功，执行业务
            Map<String, List<Catelog2Vo>> dataFromDB = getDataFromDB();
            String lock1 = redisTemplate.opsForValue().get("lock");
            if(Objects.equals(uuid,lock1)) {
                redisTemplate.delete("lock");//释放锁
            }
            return dataFromDB;
        }else {//加锁失败，执行重试
            // 可以设置休眠时间
            return getCatalogJsonFromDBWithRedisLock();//自旋锁
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        ……
        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
        return result;
    }
```

### 优化八：分布式锁5

```java
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
        return collect;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 1. 占分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);//加锁并设置过期时间
        if(lock){//加锁成功，执行业务
            Map<String, List<Catelog2Vo>> dataFromDB = getDataFromDB();
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);//lua脚本原子性删除
            return dataFromDB;
        }else {//加锁失败，执行重试
            // 可以设置休眠时间
            return getCatalogJsonFromDBWithRedisLock();//自旋锁
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        ……
        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
        return result;
    }
```

### 优化九：分布式锁6

```java
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
        return collect;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 1. 占分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);//加锁并设置过期时间
        if(lock){//加锁成功，执行业务
           Map<String, List<Catelog2Vo>> dataFromDB = null;
           try {
               dataFromDB = getDataFromDB();
           }finally {
              String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
              redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);//lua脚本原子性删除
           }
        }else {//加锁失败，执行重试
            // 可以设置休眠时间
            return getCatalogJsonFromDBWithRedisLock();//自旋锁
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        ……
        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
        return result;
    }
```

### 优化十：分布式锁7

```java
private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        ……
        return collect;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLock() {
        // 1. 从redisson获取锁
        RLock lock = redissonClient.getLock("catalogJson-lock");//锁的名字代表锁的粒度，越细越快，一般约定:具体缓存的某个数据
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDB = null;
        try {
            dataFromDB = getDataFromDB();
        }finally {
            lock.unlock();
        }
        return dataFromDB;
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        ……
    }

    /**
     * 优化查询2：使用redis缓存
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        ……
        return result;
    }
```