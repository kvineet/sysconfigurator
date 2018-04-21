# Sysconfigurator

This is small tool developed for very niche requirement. But I am sharing it here for anyone else with same requirements.

This tool can be used to encrypt the value in database table with AES encryption. It could be usefull for storing sensetive data in database.
Your webapp only needs to know the db credentials and AES key. It can then load the sensetive data from db and decrypt it for use later.

The queries that retrive primary keys for table are hardcoded for postgres, so if you want to use it for any other db, you will need to change those queries.
