<template>
  <div class="search-container">
    <h2>Search Flights</h2>
    <div class="input-group">
      <label>From:</label>
      <input v-model="departureCity" placeholder="Departure City" @change="fetchAirportICAO('departure')" />
    </div>
    <div class="input-group">
      <label>To:</label>
      <input v-model="arrivalCity" placeholder="Arrival City" @change="fetchAirportICAO('arrival')" />
    </div>
      <h3>Select Travel Date</h3>
      <Calendar :available-dates="availableDates" @select-date="searchFlights" />
    <div v-if="flights.length">
      <h3>Available Flights</h3>
      <ul>
        <li v-for="flight in sortedFlights" :key="flight.id">
          {{ flight.time }} - {{ flight.cost }} USD
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import Calendar from "../components/Calender.vue";
import axios from "axios";

export default {
  components: { Calendar },
  data() {
    return {
      departureCity: "",
      arrivalCity: "",
      departureICAO: "",
      arrivalICAO: "",
      availableDates: [],
      flights: []
    };
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
    async fetchAirportICAO(type) {
      const city = type === "departure" ? this.departureCity : this.arrivalCity;
      if (!city) return;

      try {
        const response = await axios.get(`https://api.aviationstack.com/v1/airports?access_key=f6cad07d84532d5969df8714f1c19fb2&city=${city}`);
        if (response.data.data.length > 0) {
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
        const response = await axios.get(`https://api.aviationstack.com/v1/flights?access_key=f6cad07d84532d5969df8714f1c19fb2&dep_iata=${this.departureICAO}&arr_iata=${this.arrivalICAO}`);
        const flightDates = new Set(response.data.data.map(flight => flight.flight_date));

        this.availableDates = Array.from(flightDates);
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
          cost: Math.floor(Math.random() * 500) + 50 // Mock price
        }));
      } catch (error) {
        console.error("Error fetching flights:", error);
      }
    }
  }
};
</script>
