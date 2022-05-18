package com.example.graphqlandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {
    private val launches = mutableListOf<LaunchListQuery.Launch>()
    private val adapter = LaunchListAdapter(launches)



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

                val newLaunches = response.data?.launches?.launches?.filterNotNull()
              //  val adapter = newLaunches?.let { LaunchListAdapter(it) }
                recyclerview.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerview.adapter = adapter

                if (newLaunches != null) {
                    launches.addAll(newLaunches)
                    adapter.notifyDataSetChanged()
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
}
