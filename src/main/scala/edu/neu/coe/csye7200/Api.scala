package edu.neu.coe.csye7200

import requests._
import ujson._

object Api extends App {
  val playlistId = "5Rrf7mqN8uus2AaQQQNdc1"
  val clientId = "62cb8cd2285a4715b7652e2d9c18e2d5"
  val clientSecret = "dad178c35e7d45048c5e57e81ee80149"
  val token = getAccessToken()

  // Fetching Access Token
  def getAccessToken(): String = {
    val response = requests.post(
      "https://accounts.spotify.com/api/token",
      headers = Map("Content-Type" -> "application/x-www-form-urlencoded"),
      data = Map(
        "grant_type" -> "client_credentials",
        "client_id" -> clientId,
        "client_secret" -> clientSecret
      )
    )

    val json = ujson.read(response.text())
    json("access_token").str
  }

  // Fetching Artist Data
  def fetchArtistData(artistId: String): Value = {
    val response = requests.get(
      s"https://api.spotify.com/v1/artists/$artistId",
      headers = Map("Authorization" -> s"Bearer $token")
    )
    ujson.read(response.text())
  }

  // Fetching Playlist Data
  def fetchPlaylistData(): Value = {
    val response = requests.get(
      s"https://api.spotify.com/v1/playlists/$playlistId",
      headers = Map("Authorization" -> s"Bearer $token")
    )
    ujson.read(response.text())
  }

  // Analysis
  val playlistData = fetchPlaylistData()

  // Extract song details
  val songs = playlistData("tracks")("items").arr.map { item =>
    val track = item("track")
    val name = track("name").str
    val duration = track("duration_ms").num.toLong
    val artists = track("artists").arr.map(a => (a("name").str, a("id").str))
    (
      name,
      duration,
      artists
    )
  }

  // Top 10 longest songs
  val topSongs = songs.sortBy(-_._2).take(10)

  // Printing top songs
  println("------------Top 10 Longest Songs---------------")
  topSongs.foreach { case (name, duration, _) =>
    println(s"$name, $duration")
  }

  // Fetch artist details
  val artistsFromTopSong = topSongs.flatMap(_._3).distinct
  println(artistsFromTopSong)
  val artistDetails = artistsFromTopSong.map { case (name, id) =>
    val artistData = fetchArtistData(id)
    val followers = artistData("followers")("total").num.toLong
    (name, followers)
  }

  // Sort artists by follower count
  val sortedArtists = artistDetails.sortBy(-_._2)

  println()
  println("------------Artists and Their Followers---------------")
  sortedArtists.foreach { case (name, followers) =>
    println(s"$name, $followers")
  }
}
