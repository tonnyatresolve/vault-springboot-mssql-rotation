# Demo of Zero down-time dynamic credential rotation Vault database secret engine to connect to Microsoft SQL Server

## Environment variables
- MSSQL_CONNECTION_STRING=jdbc:sqlserver://192.168.114.106:1433;databaseName=app-test-db;encrypt=false;<br />
- VAULT_DATABASE_CONNECTION=mssql - database connection name in Vault<br />
- VAULT_DATABASE_ROLE=mssql-role - database role name in Vault<br />
- VAULT_SERVER_ADDRESS=ent-vault.demo.reslv.one - Vault server FQDN<br />
- VAULT_SERVER_PORT=8200 - Vault server port<br />
- VAULT_SERVER_NAMESPACE=ocp - Vault namespace<br />
- VAULT_APPROLE_ID=ed4a32c8-2a56-c4b9-13cf-dcb87ce1450d - AppRole ID to authenticate to Vault<br />
- VAULT_APPROLE_SECRET_ID=54a893f2-c50d-416b-a8c5-5b703fd921af - AppRole secret ID to authenticate to Vault<br />

## API Endpoint
http://[hostname]/api/v1/companies