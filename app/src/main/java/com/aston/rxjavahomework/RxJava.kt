package com.aston.rxjavahomework

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.internal.operators.observable.ObservableFromCallable
import io.reactivex.internal.operators.observable.ObservableFromIterable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import kotlin.random.Random



fun main() {
//    fromIterable()
//    fromCallable()
//    interval()
//    create()
//    observableAndThreeObservers()
//    concat()
//    merge()
//    zip()
    caesarСipher()

    while (true) {

    }
}

fun caesarСipher() {
    while (true){
        println("input line(characters from a to z and A to Z will be encrypted):")
        val input = readLine()!!
        val wordsObservable = Observable.just(input)
        val cipherObservable: Observable<String> = wordsObservable.flatMap {data->
            Observable.create{ emitter->
                emitter.onNext(data.map { transformChar(it) }.joinToString(separator = ""))
            }
        }
        val disposable = cipherObservable.subscribe {
            println("result:")
            println(it)
        }
    }
}

fun transformChar(c: Char): Char{
    if (c in 'a'..'z'){
        if (c == 'z') return 'a'
        return (c.code + 1).toChar()
    }
    if (c in 'A'..'Z'){
        if (c == 'Z') return 'A'
        return (c.code + 1).toChar()
    }
    return c
}

fun fromIterable() {
    val list = listOf(1, 2, 3, 4, 5)
    val observable = ObservableFromIterable(list)
    val disposable = observable.subscribe { item ->
        println(item)
    }
}

fun fromCallable() {
    val callable = Callable {
        Thread.sleep(1000)
        45
    }
    val disposable = ObservableFromCallable(callable)
        .subscribe {
            println(it)
        }
}

fun interval() {
    val observable = Observable.interval(1, TimeUnit.SECONDS)
    val disposable = observable.subscribe {
        println(it)
    }
}

fun create() {
    val thread = Thread {
        val disposable = Observable.create<Int> { emitter ->
            repeat(5) {
                Thread.sleep(500)
                emitter.onNext(Random.nextInt(0, 100))
            }
            emitter.onComplete()
        }.subscribe({
            println(it)
        }, {
            println(it)
        }, {
            println("completed")
        })
    }.apply { start() }
    thread.join()
}

fun observableAndThreeObservers() {
    val observable = Observable.create<Int> { emitter ->
        for (i in 1..100) {
            emitter.onNext(i)
        }
        emitter.onComplete()
    }
    val firstObs =
        observable.filter { it % 2 == 0 }.subscribe { println("first observable: $it") }
    val secondObs =
        observable.takeLast(10).subscribe { println("second observable: $it") }
    val thirdObs =
        observable.filter { it % 3 == 0 && it % 5 == 0 }
            .subscribe { println("third observable: $it") }

}

fun concat() {
    val disposable = Observable.concat(getFirst(), getSecond())
        .subscribe({
            println(it)
        }, {
           println(it)
        }, {
            println("completed")
        })
}

fun merge(){
    val disposable = Observable.merge(getFirst(), getSecond())
        .subscribe({
            println(it)
        }, {
            println(it)
        }, {
            println("completed")
        })
}

fun getFirst(): Observable<Int>{
    return Observable.create{ emitter ->
        var i = 0
        repeat(50) {
            emitter.onNext(i++)
            Thread.sleep(100)
        }
        emitter.onComplete()
    }.subscribeOn(Schedulers.newThread())
}

fun getSecond(): Observable<Int>{
    return Observable.create<Int?> { emitter ->
        var i = 50
        repeat(50) {
            emitter.onNext(i++)
            Thread.sleep(100)
        }
        emitter.onComplete()
    }.subscribeOn(Schedulers.newThread())
}

fun zip(){
    val observableOne = Observable.fromIterable(listOf(12,4,8))
    val observableTwo = Observable.fromIterable(listOf(3,2,1))
    val observableF: Observable<Long> = Observable.zip(observableOne, observableTwo, BiFunction { t1, t2 ->
        return@BiFunction twoNumbers(t1, t2)
    })
    val disposable = observableF.subscribe {
        println(it)
    }
}

fun twoNumbers(first: Int, second: Int): Long {
    return first.toLong() / second.toLong()
}