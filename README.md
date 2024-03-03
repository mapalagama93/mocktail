
![Screenshot-2024-03-03-132528.png](https://postimg.cc/949yPjRG)

# Simple, Extensible Mock Server
## Run with docker
You can easily run mock server by running following docker run command. This is a springboot application. So you can use springboot ways to provide configurations to mocktail.

    docker run -p 8080:8080 devhasitha/mocktail:latest
Go to http://localhost:8080/mocktail

## Configurations

By default mocktail will use in-memory storage to store. But you can configure local file path or s3 bucket to store mock routes.

### Available Providers
specify provider using `mocktail.provider` env property.

1. S3RouteProvider
2. InMemoryRouteProvider
3. FileRouteProvider

#### FileRouteProvider
This required `mocktail.provider.FileRouteProvider.file` property to be in env.

#### S3RouteProvider
This required `mocktail.provider.S3RouteProvider.bucket` and `mocktail.provider.S3RouteProvider.file` property to be in env. 
