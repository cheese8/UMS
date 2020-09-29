# permission example
## 添加用户, 默认密码: admin
http://localhost:9090/demo/addUser/13322221111
## 添加权限
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/permission/**&restfulMethod=post
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/deny/**&restfulMethod=post
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/role/admin/**&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/role/user/**&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/auth/admin/**&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/permission/**&restfulMethod=post
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/role/admin/**&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/role/user/**&restfulMethod=get
## 删除权限
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/permission/**&restfulMethod=post
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/deny/**&restfulMethod=post
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/role/admin/**&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/role/user/**&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/auth/admin/**&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_USER?uri=/test/permission/**&restfulMethod=post
http://localhost:9090/demo/delPermissionData/ROLE_USER?uri=/test/role/admin/**&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_USER?uri=/test/role/user/**&restfulMethod=get
## 测试权限控制
### 过滤器模式: PermissionController: @EnableUriAuthorize(filterOrInterceptor = true)
http://localhost:9090/demo/test/permission/1
http://localhost:9090/demo/test/deny/1
http://localhost:9090/demo/test/pass/1
http://localhost:9090/demo/test/role/admin/1
http://localhost:9090/demo/test/role/user/1
http://localhost:9090/demo/test/auth/admin/1


### 拦截器(注解)模式: PermissionController: @EnableUriAuthorize(filterOrInterceptor = false)
http://localhost:9090/demo/test/permission/1
http://localhost:9090/demo/test/deny/1
http://localhost:9090/demo/test/pass/1
http://localhost:9090/demo/test/role/admin/1
http://localhost:9090/demo/test/role/user/1
http://localhost:9090/demo/test/auth/admin/1