# SysConfigurator

This is small tool developed for very niche requirement. But I am sharing it here for anyone else with same requirements.

This tool can be used to encrypt the value in database table with AES encryption. It could be useful for storing sensitive data in database.
Your web-app only needs to know the DB credentials and AES key. It can then load the sensitive data from DB and decrypt it for use later.

The queries that retrieve primary keys for table are hard-coded for PostgreSQL, so if you want to use it for any other DB, you will need to change those queries.

## IMPORTANT NOTES

### Important note for recent configurations

- While making jar executable, make sure to add write permissions. Use 'chmod +xw ./sysconfigurator.jar'
- It will allow saving recent configurations within same directory within file named `sysconfigurator.cfg`.

### JSON Input  

You can use JSON instead of typing in all the input fields.

``` json
{
  "aesKey": "test",
  "dbUrl": "jdbc:postgresql://172.17.0.3:5432/postgres",
  "dbUserName": "postgres",
  "dbPassword": "postgres",
  "tableName": "system_config"
}
```

### Screenshot  

![Example](./screenshot/example.jpg?raw=true)