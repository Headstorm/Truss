package com.headstorm.service

import org.http4s.websocket.WebSocketFrame

class DataService[F[_]]() {

  def compose(wsf: WebSocketFrame): Unit =
    println(wsf.data.toString)

  def counties: List[String] = List("QA","DE","GB","MJ","PT","DG","BD","AS","PE")

  def countyWeatherDemo: String = {
    """{
      |"features": [
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC013",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC013",
      |        "@type": "wx:Zone",
      |        "id": "AKC013",
      |        "type": "county",
      |        "name": "Aleutians East",
      |        "state": "AK",
      |        "cwa": [
      |          "AFC"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFC"
      |        ],
      |        "timeZone": [
      |          "America/Anchorage",
      |          "America/Nome"
      |        ]
      |      }
      |    },
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC016",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC016",
      |        "@type": "wx:Zone",
      |        "id": "AKC016",
      |        "type": "county",
      |        "name": "Aleutians West",
      |        "state": "AK",
      |        "cwa": [
      |          "AFC"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFC"
      |        ],
      |        "timeZone": [
      |          "America/Nome",
      |          "America/Adak"
      |        ]
      |      }
      |    },
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC020",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC020",
      |        "@type": "wx:Zone",
      |        "id": "AKC020",
      |        "type": "county",
      |        "name": "Anchorage",
      |        "state": "AK",
      |        "cwa": [
      |          "AFC"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFC"
      |        ],
      |        "timeZone": [
      |          "America/Anchorage"
      |        ]
      |      }
      |    },
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC050",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC050",
      |        "@type": "wx:Zone",
      |        "id": "AKC050",
      |        "type": "county",
      |        "name": "Bethel",
      |        "state": "AK",
      |        "cwa": [
      |          "AFC",
      |          "AFG"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFC",
      |          "https://api.weather.gov/offices/AFG"
      |        ],
      |        "timeZone": [
      |          "America/Anchorage",
      |          "America/Nome"
      |        ]
      |      }
      |    },
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC060",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC060",
      |        "@type": "wx:Zone",
      |        "id": "AKC060",
      |        "type": "county",
      |        "name": "Bristol Bay",
      |        "state": "AK",
      |        "cwa": [
      |          "AFC"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFC"
      |        ],
      |        "timeZone": [
      |          "America/Anchorage"
      |        ]
      |      }
      |    },
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC068",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC068",
      |        "@type": "wx:Zone",
      |        "id": "AKC068",
      |        "type": "county",
      |        "name": "Denali",
      |        "state": "AK",
      |        "cwa": [
      |          "AFC",
      |          "AFG"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFC",
      |          "https://api.weather.gov/offices/AFG"
      |        ],
      |        "timeZone": [
      |          "America/Anchorage"
      |        ]
      |      }
      |    },
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC070",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC070",
      |        "@type": "wx:Zone",
      |        "id": "AKC070",
      |        "type": "county",
      |        "name": "Dillingham",
      |        "state": "AK",
      |        "cwa": [
      |          "AFC"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFC"
      |        ],
      |        "timeZone": [
      |          "America/Anchorage"
      |        ]
      |      }
      |    },
      |    {
      |      "id": "https://api.weather.gov/zones/county/AKC090",
      |      "type": "Feature",
      |      "geometry": null,
      |      "properties": {
      |        "@id": "https://api.weather.gov/zones/county/AKC090",
      |        "@type": "wx:Zone",
      |        "id": "AKC090",
      |        "type": "county",
      |        "name": "Fairbanks North Star",
      |        "state": "AK",
      |        "cwa": [
      |          "AFG"
      |        ],
      |        "forecastOffices": [
      |          "https://api.weather.gov/offices/AFG"
      |        ],
      |        "timeZone": [
      |          "America/Anchorage"
      |        ]
      |      }
      |    }
      |    ]
      |    }
      |""".stripMargin
  }
}
