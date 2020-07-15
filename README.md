# README
## API
- 登录
发送
```json
{
"user_name": String,
name: String,
password: Hash
}
```
返回（status==1表示登录成功，status==0表示失败）
```json
{
status: int,
name: String
}
```
- 签到地点
```json
{
pos_id: int,
pos_name: String,
key: String
}
```
- 签到记录
```json
{
user_name: String,
pos_id: int,
time: String,
device_id: String
}
```
## To Do
- 加密（用户密码目前还是直接保存在文件里）
- 获取设备ID
- 访问网络似乎都需要开多线程
