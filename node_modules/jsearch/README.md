# jsearch - Simplified searcher on Google, Yandex and Bing

[![npm package](https://nodei.co/npm/jsearch.png?downloads=true&downloadRank=true&stars=true)](https://nodei.co/npm/jsearch/)

## Install

```js

	npm install jsearch

```

---

## Super Simple to Use

jSearch is designed to be the simplest way possible to make search on populer search engines; Google, Yandex and Bing. It  uses node's http and https modules.

```js

	var js = require('jsearch');

	js.google('queryStringYouWant',10,function(response){
		console.log(response) // Show the links for 10 pages on Google
	})

```

---

## Completely usage

It has 3 methods for 3 search engines. On every methods;

- First parameter is what you want to search.
- Second parameter is how many page to search.
- Third and last parameter is to callback for the results. 

Note : Result is an array !


```js
	
	var js = require('jsearch');
	
	js.google('queryStringYouWant',10,function(response){
		console.log(response) // for Google results
	})
	
	js.yandex('queryStringYouWant',10,function(response){
		console.log(response) // for Yandex results
	})
	
	js.bing('queryStringYouWant',10,function(response){
		console.log(response) // for Bing results
	})
	
```

---

## Google, Yandex and Bing

jSearch supports 3 search engines now. But it is developing for other engines. Soon, it will support more search engines.

---

## Changes

Follow the changes on [jSearch Docs](http://janissaries.org/jsearch/)

