# Exposed Power User Utils

Contains general purpose extensions and utilities for Exposed, not bound to a specific SQL dialect.

* Suspendable Nested Transactions
    * Don't you hate when your application gets into a deadlock because you mistakenly called `transaction` within a `transaction`, so there isn't any available connections for the inner transaction to use, but the outer transaction is waiting for the inenr transaction to finish? Suspendable Nested Transactions™ fixes this issue by storing the current transaction state on the Coroutine Context, so if you call the transaction block again within a transaction, the already existing transaction will be reused!
    * This also handles repeating your query when it fails, which is useful if your query failed due to repeatable reads.