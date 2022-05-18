package com.example.graphqlandroid

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient

val okHttpClient = OkHttpClient.Builder()
    .build()

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com/graphql")
    .okHttpClient(okHttpClient)
    .build()