# Generic-Database-Operation
Generic Utility to perform Insert/Update operation on any table using excel sheet

##### Purpose : 
Generic Utility to perform Insert/Update operation on any table using excel sheet.Now no need worry about creating sql statements in order to update/insert records on table. You just need to fill  your records into an excel sheet, excel sheet that needs to be uploaded can be downloaded from same the front end.

##### How to Use : 
First select the table on which you want to perform operation( respective tables need to be mentioned in properties file genericdboperation.properties in order to be available for operations)

```
#Generic DB Operation
generic.updation.tablelist=Persons
```
Once you have selected the table, download the template by click download template checkbox. Excel file will be downloaded in which you need to specify the data against the columns mentioned. In order to identify the colum group for an table, we use the concept of name range in excel.

######For an Insert operation :
```Insert``` named range is used in order to group all the columns that need to be inserted .

######For an Update operation
```Condtions``` named range is used for columns that are part of where clause and ```Values``` is used for columns that are part of set clause.

###### Please note that if the name range in excel is corrupted, utility will not work as expected.

