package com.example.koselig


import android.location.Address
import android.location.Location
import android.telecom.Call
import android.util.EventLogTags
import android.util.Log
import android.widget.TextView
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class WMTAManager {

    // OkHttp is a library used to make network calls
    private val okHttpClient: OkHttpClient

    private var oAuthToken: String? = null

    // This runs extra code when WMTAManager is created (e.g. the constructor)
    init {
        val builder = OkHttpClient.Builder()

        // This sets network timeouts (in case the phone can't connect
        // to the server or the server is down)
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)

        // This causes all network traffic to be logged to the console
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        okHttpClient = builder.build()
    }

    //get closest station
    fun retrievePath(
        lat: String,
        lon: String,
        successCallback: (List<String>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        //get url request to find ending station
        //variable to hold last station
        var urlrequest: String = "https://api.wmata.com/Rail.svc/json/jStationEntrances?Lat=$lat&Lon=$lon&Radius=800"

        // Building the request, passing url in
        val request = Request.Builder()
            .url(urlrequest)
            .header("api_key", "74312f5efeac405b89c69cfeb6cd18bf")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Same error handling to last time
                errorCallback(e)
            }

            override fun onResponse(call: Call, response: Response) {
                // Similar success / error handling to last time
                val alerts = mutableListOf<Alert>()
                val responseString = response.body()?.string()

                if (response.isSuccessful && responseString != null) {
                    print("if")
                    val stationlist = JSONObject(responseString).getJSONArray("Entrances")
                    if (stationlist.length() > 0) {
                        //only want the first value
                        val curr = stationlist.getJSONObject(0)
                        val endingstation: String = curr.getString("StationCode1")
                        Log.d("value =", "$endingstation")
                        //get url request to find final station
                        urlrequest = "https://api.wmata.com/Rail.svc/json/jPath?FromStationCode=C04&ToStationCode=$endingstation"
                        //urlrequest.plus(endingstation)

                        val request2 = Request.Builder()
                            .url(urlrequest)
                            .header("api_key", "74312f5efeac405b89c69cfeb6cd18bf")
                            .build()

                        //get path
                        okHttpClient.newCall(request2).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                // Same error handling to last time
                                errorCallback(e)
                            }

                            override fun onResponse(call: Call, response: Response) {
                                // Similar success / error handling to last time
                                val path = mutableListOf<String>()
                                val responseString2 = response.body()?.string()

                                if (response.isSuccessful && responseString2 != null) {

                                    val stationlist = JSONObject(responseString2).getJSONArray("Path")
                                    if (stationlist.length() > 0) {
                                        for (i in 0 until stationlist.length()) {
                                            val curr = stationlist.getJSONObject(i)
                                            val stationLine: String = curr.getString("LineCode")
                                            val stationName: String = curr.getString("StationName")
                                            val station = "$stationLine: $stationName"
                                            path.add(station)
                                        }
                                    }

                                    successCallback(path)
                                    //...
                                } else {
                                    // Invoke the callback passed to our [retrieveTweets] function.
                                    errorCallback(Exception("Creating path call failed"))
                                }
                            }
                        })


                    }
                    //...
                } else {
                    print("else")
                    // Invoke the callback passed to our [retrieveTweets] function.
                    errorCallback(Exception("Starting station call failed"))
                }
            }
        })

    }

    //get alerts
    fun retrieveAlerts(
        successCallback: (List<Alert>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        // Building the request, passing the OAuth token as a header
        val request = Request.Builder()
            .url("https://api.wmata.com/Incidents.svc/json/Incidents")
            .header("api_key", "74312f5efeac405b89c69cfeb6cd18bf")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Same error handling to last time
                errorCallback(e)
            }

            override fun onResponse(call: Call, response: Response) {
                // Similar success / error handling to last time
                val alerts = mutableListOf<Alert>()
                val responseString = response.body()?.string()

                if (response.isSuccessful && responseString != null) {
                    //fake json
                    val fakeJson = """
                        {
                            "Incidents": [
                                {
                                    "DateUpdated": "2010-07-29T14:21:28",
                                    "DelaySeverity": null,
                                    "Description": "Red Line: Expect residual delays to Glenmont due to an earlier signal problem outside Forest Glen.",
                                    "EmergencyText": null,
                                    "EndLocationFullName": null,
                                    "IncidentID": "3754F8B2-A0A6-494E-A4B5-82C9E72DFA74",
                                    "IncidentType": "Delay",
                                    "LinesAffected": "RD;",
                                    "PassengerDelay": 0,
                                    "StartLocationFullName": null
                                }
                            ]
                        }
                        """
                    val alertlist = JSONObject(responseString).getJSONArray("Incidents")
                    if (alertlist.length() > 0) {
                        for (i in 0 until alertlist.length()) {
                            val curr = alertlist.getJSONObject(i)
                            val titleAlert = curr.getString("IncidentType")
                            val Description = curr.getString("Description")
                            alerts.add(
                                com.example.gwuexplorersutton.Alert(
                                    titleAlert = titleAlert,
                                    contentAlert = Description
                                )
                            )
                        }
                    }

                    successCallback(alerts)
                    //...
                } else {
                    // Invoke the callback passed to our [retrieveTweets] function.
                    errorCallback(Exception("Search Alerts call failed"))
                }
            }
        })
    }

