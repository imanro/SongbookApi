# SongbookApi

## Importer

Run import like this:


```
gradle runImport -Pargs=--m=song
```

## Testing

### Integration tests

Run integration tests of songbook-common module:

```
gradle it
```

It will also clean the test database

### Unit tests

```
gradle test
```

To execute specific test, use command

```
gradle test --tests SomeTestClass
```

This is applicable to integration tests either

