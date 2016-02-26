# Reactive Blog (unfinished)
Using akka-http, PostgreSQL and PostgreSQL-async (https://github.com/mauricio/postgresql-async),
this actually is a fully reactive, non-blocking scala REST API.

Using, among others spray-json for serialization / deserialization of objects, and
https://github.com/wix/accord for validation.

Also demonstrates the elegant use of rejection handlers to give users feedback about bad input.

As with any sane API, this uses https://github.com/swagger-akka-http/swagger-akka-http to document the APIs
and serve a swagger.json file.

the API lives on https://shielded-plains-3484.herokuapp.com/
