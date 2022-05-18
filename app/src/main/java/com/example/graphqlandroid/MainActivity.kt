package com.example.graphqlandroid

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {
    private val launches = mutableListOf<LaunchListQuery.Launch>()
    val adapter = LaunchListAdapter(launches)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerview = findViewById<RecyclerView>(R.id.launches)

        val channel = Channel<Unit>(Channel.CONFLATED)

        // Send a first item to do the initial load else the list will stay empty forever
        channel.trySend(Unit)
        adapter.onEndOfListReached = {
            channel.trySend(Unit)
        }

        lifecycleScope.launchWhenResumed {
           // fetchData()
            var cursor: String? = null
            for (item in channel) {
                val response =
                    apolloClient.query(LaunchListQuery(Optional.Present(cursor)))
                        .execute()
            /*   val response = try {
                    apolloClient.query(LaunchListQuery(Optional.Present(cursor)))
                        .execute()
                } catch (e: ApolloException) {
                    Log.d("LaunchList", "Failure", e)
                    return@launchWhenResumed
                }*/


                val newLaunches = response.data?.launches?.launches?.filterNotNull()
              //  val adapter = newLaunches?.let { LaunchListAdapter(it) }
                recyclerview.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerview.adapter = adapter

                if (newLaunches != null) {
                    launches.addAll(newLaunches)
                    adapter?.notifyDataSetChanged()
                }

                cursor = response.data?.launches?.cursor
                if (response.data?.launches?.hasMore != true) {
                    break
                }
            }

            adapter.onEndOfListReached = null
            channel.close()
        }
    }

    private suspend fun fetchData() {
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.launches)
        val response = apolloClient.query(LaunchListQuery()).execute()
        val newLaunches = response.data?.launches?.launches?.filterNotNull()
        val adapter = newLaunches?.let { LaunchListAdapter(it) }
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        Log.d("LaunchList", "Success ${response.data}")
        if (newLaunches != null) {
            launches.addAll(newLaunches)
            adapter?.notifyDataSetChanged()
        }


    }
}
