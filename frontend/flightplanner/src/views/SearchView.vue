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

    <div class="input-group">
      <label>Departure Date:</label>
      <Calendar v-model="beginDate" />
    </div>

    <div class="input-group">
      <label>Return Date:</label>
      <Calendar v-model="endDate" />
    </div>

    <div class="input-group">
      <label>Passengers:</label>
      <input type="number" v-model="passengerCount" min="1" />
    </div>

    <button @click="searchFlights">Search Flights</button>

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
import Calendar from "@/components/Calender.vue";
import axios from "axios";

export default {
  components: { Calendar },
  data() {
    return {
      departureCity: "",
      arrivalCity: "",
      departureICAO: "",
      arrivalICAO: "",
      beginDate: "",
      endDate: "",
      passengerCount: 1,
      flights: []
    };
  },
  computed: {
    sortedFlights() {
      return this.flights.sort((a, b) => {
        if (a.time !== b.time) return a.time.localeCompare(b.time);
        return a.cost - b.cost;
      });
    }
  },
  methods: {
    async fetchAirportICAO(type) {
      const city = type === "departure" ? this.departureCity : this.arrivalCity;
      if (!city) return;

      try {
        const response = await axios.get(`https://api.aviationstack.com/v1/airports?access_key=YOUR_API_KEY&city=${city}`);
        if (response.data.data.length > 0) {
          const icaoCode = response.data.data[0].icao_code;
          if (type === "departure") {
            this.departureICAO = icaoCode;
          } else {
            this.arrivalICAO = icaoCode;
          }
        } else {
          alert("No airport in this city.");
        }
      } catch (error) {
        console.error("ICAO code error:", error);
      }
    },

    async searchFlights() {
      if (!this.departureICAO || !this.arrivalICAO || !this.beginDate || !this.endDate) {
        alert("Fill all fields.");
        return;
      }

      try {
        const response = await axios.get(
            `https://opensky-network.org/api/flights/departure?airport=${this.departureICAO}&begin=1700000000&end=1700003600`
        );

        this.flights = response.data.map(flight => ({
          id: flight.icao24,
          time: new Date(flight.firstSeen * 1000).toLocaleString(),
          cost: Math.floor(Math.random() * 500) + 50 // Random prce
        }));
      } catch (error) {
        console.error("Couldn't load flights:", error);
      }
    }
  }
};
</script>
