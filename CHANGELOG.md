Changelog
=========
#### 1.2.0

* Change: targetVersion/compileVersion update to 31
* Change: AGP is updated to 7.1.1
* Change: MDC is update to 1.5.0

#### 1.1.2

* Fix: Deadlock in `PagedListModelCache`
* Change: Provide correct coroutine scope for `PagedListModelCache`

#### 1.1.1

* Fix: Fix `JobCancellationException` cased by sending element to closed channel

#### 1.1.0

* Change: Remove `workerDispatcher` from `PagedListLoungeController` constructor
* Fix: Fix internal `IndexOutOfBoundsException` in `PagedListModelCache`

#### 1.0.0

* First release
