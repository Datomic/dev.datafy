# dev.datafy

## Rationale

Bring datafy and nav to commonly used mechanism types. Currently
covers some of Java, core.async, and reify.

Originally developed for use in
[REBL](https://docs.datomic.com/cloud/other-tools/REBL.html) when
working on Datomic.

## Getting It

In deps.edn

    com.datomic/dev.datafy {:git/sha "4a9dffb" 
                                :git/tag "v0.1"
	                        :git/url "git@github.com:Datomic/dev.datafy.git"}


## Example Usage

From your REPL, call

    (datomic.dev.datafy/datafy!)

See [siderail/dev_datafy_examples.repl](https://github.com/Datomic/dev.datafy/blob/main/siderail/dev_datafy_examples.repl) for some things to view in a
datafy/nav aware viewer.
