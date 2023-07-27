# tapestry-boot
spring boot + Tapestry (+ Dubbo)

## Tapestry
Tapestry框架在设计上非常灵活和可扩展，可以轻松地自定义组件、服务和过滤器等。这使得Tapestry框架可以适应各种不同的应用场景和需求，从而提高了其技术的流行度。
尽管Tapestry框架具有一定的优势和特点，但是在面对越来越激烈的竞争和新技术的冲击时，其技术的流行度逐渐下降。
现在Tapestry生态环境已经鲜有血液注入，拥抱新的技术，或许能让它不被遗忘
如今Spring生态占据了绝大部分市场，Tapestry框架曾经也提供过和Spring的整合。
在Tapestry 5中，可以使用Tapestry-Spring模块来实现Tapestry和Spring的整合。Tapestry-Spring模块提供了一些注解和工具类，可以方便地将Spring管理的Bean注入到Tapestry组件中，同时也可以方便地使用Spring的事务管理和AOP功能。

需要注意的是，虽然Tapestry可以与Spring整合，但是并不是必须的。Tapestry本身提供了很多强大的功能，如依赖注入、组件化开发、表单验证等，可以独立地使用。整合Spring只是为了提供更加灵活和强大的服务和支持。

这里提供了SpringBoot和Tapestry5.5版本的整合，并提供了Dubbo微服务版本的整合以供参考

模块说明：
1. common-interface: 提供公共接口，可根据项目状况自定义
2. tapestry-spring-boot-starter：自定义starter集成Tapestry
3. simple-project：Tapestry的单模块项目
4. dubbo-provider：Dubbo项目的服务提供者模块
5. dubbo-admin：Dubbo项目的Tapestry集成模块
