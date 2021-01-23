grpcurl -plaintext\
    -d '
    {
      "first_name": "Nishiyama",
      "last_name": "",
      "message": ""
    }
    '\
    -v localhost:8080 example.kt.Greeter.HelloError
