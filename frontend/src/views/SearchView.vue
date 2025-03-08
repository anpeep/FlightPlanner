<template>
  <div class="search-container">
    <h2>Search Flights</h2>

    <div class="input-group">
      <label>From:</label>
      <input v-model="departureCity" placeholder="Departure City" @input="handleCityInput('departure')" />
      <ul v-if="departureSuggestions.length" class="suggestions">
        <li v-for="(city, index) in departureSuggestions" :key="index" @click="selectCity(city, 'departure')">
          {{ city }}
        </li>
      </ul>
    </div>

    <div class="input-group">
      <label>To:</label>
      <input v-model="arrivalCity" placeholder="Arrival City" @input="handleCityInput('arrival')" />
      <ul v-if="arrivalSuggestions.length" class="suggestions">
        <li v-for="(city, index) in arrivalSuggestions" :key="index" @click="selectCity(city, 'arrival')">
          {{ city }}
        </li>
      </ul>
    </div>

    <h3>Select Travel Date</h3>


    <div v-if="flights.length">
      <h3>Available Flights</h3>
      <ul>
        <li v-for="flight in flights" :key="flight.id">
          {{ flight.time }} USD
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      departureCity: "",
      arrivalCity: "",
      departureICAO: "",
      arrivalICAO: "",
      departureSuggestions: [],
      arrivalSuggestions: [],
      availableDates: [],
      flights: []
    };
  },
  computed: {
    calendarAttributes() {
      return this.availableDates.map(date => ({
        date,
        class: "available-flight"
      }));
    }
  },
  watch: {
    async departureICAO() {
      if (this.arrivalICAO) {
        await this.fetchAvailableDates();
      }
    },
    async arrivalICAO() {
      if (this.departureICAO) {
        await this.fetchAvailableDates();
      }
    }
  },
  methods: {
    async handleCityInput(type) {
      const city = type === "departure" ? this.departureCity : this.arrivalCity;
      if (city.length < 3) {
        if (type === "departure") this.departureSuggestions = [];
        else this.arrivalSuggestions = [];
        return;
      }

      console.log(`Fetching airports for: ${city}`);

      try {
        const response = await axios.get(
            `https://api.aviationstack.com/v1/airports?access_key=f6cad07d84532d5969df8714f1c19fb2&search=${city}`
        );

        const cities = response.data.data.map(airport => airport.city).filter((v, i, a) => a.indexOf(v) === i); // Unikaalsed linnad

        console.log("Suggestions:", cities);

        if (type === "departure") {
          this.departureSuggestions = cities;
        } else {
          this.arrivalSuggestions = cities;
        }
      } catch (error) {
        console.error("Error fetching city suggestions:", error);
      }
    },

    async selectCity(city, type) {
      if (type === "departure") {
        this.departureCity = city;
        this.departureSuggestions = [];
      } else {
        this.arrivalCity = city;
        this.arrivalSuggestions = [];
      }
      await this.fetchAirportICAO(type);
    },

    async fetchAirportICAO(type) {
      const city = type === "departure" ? this.departureCity : this.arrivalCity;
      if (!city) return;

      try {
        const response = await axios.get(
            `https://api.aviationstack.com/v1/airports?access_key=f6cad07d84532d5969df8714f1c19fb2&city=${city}`
        );

        if (response.data.data.length > 0) {
          console.log("Fetched airport data:", response.data);
          const icaoCode = response.data.data[0].icao_code;
          if (type === "departure") {
            this.departureICAO = icaoCode;
          } else {
            this.arrivalICAO = icaoCode;
          }
        } else {
          alert("No airport found in this city.");
        }
      } catch (error) {
        console.error("ICAO fetch error:", error);
      }
    },

    async fetchAvailableDates() {
      try {
        const response = await axios.get(
            `https://api.aviationstack.com/v1/flights?access_key=f6cad07d84532d5969df8714f1c19fb2&dep_iata=${this.departureICAO}&arr_iata=${this.arrivalICAO}`
        );

        this.availableDates = [...new Set(response.data.data.map(flight => flight.flight_date))];
      } catch (error) {
        console.error("Error fetching available dates:", error);
      }
    },

    async searchFlights(date) {
      try {
        const response = await axios.get(
            `https://api.aviationstack.com/v1/flights?access_key=f6cad07d84532d5969df8714f1c19fb2&flight_date=${date}&dep_iata=${this.departureICAO}&arr_iata=${this.arrivalICAO}`
        );

        this.flights = response.data.data.map(flight => ({
          id: flight.flight.iata,
          time: flight.departure.estimated || flight.departure.scheduled,
        }));
      } catch (error) {
        console.error("Error fetching flights:", error);
      }
    }
  }
};
</script>

<style>
.suggestions {
  list-style: none;
  padding: 0;
  margin: 0;
  border: 1px solid #ddd;
  background: white;
  position: absolute;
  z-index: 1000;
  width: 100%;
}

.suggestions li {
  padding: 8px;
  cursor: pointer;
}

.suggestions li:hover {
  background: #f0f0f0;
}
</style>
