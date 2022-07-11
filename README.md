## 问题记录
* 如果通过token获取的user为空如何处理？
答：没有进行秒杀行为时，不影响。进行秒杀行为时，会返回登录页面。


## 优化
* Snowflake - 分布式自增 ID 算法（雪花）
* 拦截器判断是否登录


## 备忘录
* 秒杀订单建立唯一索引