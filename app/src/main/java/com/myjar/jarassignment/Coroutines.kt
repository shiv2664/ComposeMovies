package com.myjar.jarassignment

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main() = runBlocking {

    val singleDispatcher = newSingleThreadContext("MySingleThread")
    val singleIO = Dispatchers.IO.limitedParallelism(2)
    val singleDefault = Dispatchers.Default.limitedParallelism(1)


    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val job: Job = this.launch(SupervisorJob() + Dispatchers.IO) {}

    val normalJob= CoroutineScope(Dispatchers.IO)

    val coroutineDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

   /* val executor = Executors.newFixedThreadPool(1)
    val coroutineDispatcher = executor.asCoroutineDispatcher()

    coroutineDispatcher.close()
    executor.shutdown()*/


    val job1 = CoroutineScope(coroutineDispatcher).launch {
        for (i in 0..10 step 2) {
            println("even $i")
            delay(1000)

        }
    }

    val job2 = CoroutineScope(coroutineDispatcher).launch {
        for (i in 1..10 step 2) {
            delay(1000)
            println("odd $i")

        }

    }

    runBlocking {
        job1.join()
        job2.join()
    }


    /*val data = MutableLiveData<String>()

    data.observeForever {
        println("Observer got: $it")
    }

    data.postValue("A")
    data.postValue("B")
    data.postValue("C")*/

    // Flow builder that emits even and odd numbers
    /*    val numbersFlow= channelFlow {
            withContext(singleIO){
                launch {
                    for (i in 0..10 step 2) {
                        send(i)   // use send instead of emit
                        delay(1000)
                    }
                }

                launch {
                    for (i in 1..10 step 2) {
                        delay(1000)
                        send(i)   // use send instead of emit

                    }
                }
            }

            val numbers1 = flow {
                for (i in 1..5) {
                    emit(i) // emits each value sequentially
                    delay(100)
                }
            }
            numbers1.collect { println(it) }


            val numbers = flow {
                for (i in 1..100){
                    emit(i)
                delay(1000)
            }
            }

           *//* delay(100)
        job2.cancel()*//*
    }

    numbersFlow.collectLatest { value ->
        println("Collected $value on ${Thread.currentThread().name}")
    }*/


    /*supervisorScope {
        val job1=launch{
            println("Task 1 started")
            delay(100)
            throw RuntimeException("Task 1 failed")
        }

        val job2=launch {
            println("Task 2 started")
            delay(500)
            println("Task 2 finished")
        }
    }
    println("All done")*/

}
