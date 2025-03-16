<template>
  <v-container>

    <v-row>
      <v-col class="mb-4" cols="4" md="4">
        <v-text-field
            v-model="filters.startDate"
            color="primary"
            dense
            label="Search From Date"
            outlined
            type="date"
            @input="applyFilters"
        ></v-text-field>
      </v-col>
      <v-col cols="4">
        <h3 class="text-center mt-5">Choose Details</h3>
      </v-col>
      <v-col class="mb-4" cols="4" md="4">
        <v-text-field
            v-model="filters.endDate"
            color="primary"
            dense
            label="To Date"
            outlined
            type="date"
            @input="applyFilters"
        ></v-text-field>
      </v-col>
    </v-row>

    <v-row>
      <v-col class="mb-4" cols="4" md="4">
        <v-text-field
            v-model="filters.departureCity"
            dense
            label="Departure City"
            outlined
        />
      </v-col>

      <v-col class="mb-4" cols="12" md="4">
        <v-col class="text-left" cols="12">
          <span>{{ Math.round(filters.priceRange[0]) }} €</span>
        </v-col>
        <v-range-slider
            v-model="filters.priceRange"
            :max="maxPrice"
            :min="minPrice"
            :step="10"
            :thumb-size="24"
            class="price-slider"
            thumb-label
        />
        <v-col class="text-right" cols="12">
          <span>{{ Math.round(filters.priceRange[1]) }} €</span>
        </v-col>

      </v-col>
      <v-col class="mb-4" cols="4" md="4">
        <v-text-field
            v-model="filters.arrivalCity"
            dense
            label="Arrival City"
            outlined
        />
      </v-col>
    </v-row>

    <v-col cols="12">
      <h3 class="text-center mt-5">Choose a Flight</h3>
    </v-col>
    <v-row>
      <v-col v-if="filteredFlights.length === 0" cols="12">
        <v-alert color="light-blue" outlined type="info">
          No flights to those filters right now, come back later!
        </v-alert>
      </v-col>

      <v-col v-for="flight in filteredFlights" v-if="filteredFlights.length > 0" :key="flight.id" cols="12" md="4">
        <v-card class="ma-2">
          <v-card-title>
            <span class="headline">{{ flight.departureCity + ' -> ' + flight.arrivalCity }}</span>
          </v-card-title>
          <v-card-subtitle>{{ formatDepartDate(flight.departOn) + ' -> ' + formatArriveDate(flight.arriveOn) }}
          </v-card-subtitle>

          <v-card-text>
            <p><strong>Flight duration:</strong> {{ calculateDuration(flight.departOn, flight.arriveOn) }} hours</p>
            <p><strong>Price:</strong> €{{ Math.round(flight.price) }}</p>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" @click="goToSeatSelection(flight)">To Seats</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from "axios";
import {useRoute, useRouter} from "vue-router";

export default {
  setup() {
    const router = useRouter();
    const route = useRoute();
    const planeId = route.query.planeId;

    const goToSeatSelection = (flight) => {
      axios.post("/api/seats", null, {
        params: {
          flightId: flight.id,
          planeId: flight.planeId,
        },
      });
      router.push({
        path: "/seats",
        query: {
          flightId: flight.id,
          planeId: flight.planeId,
        },
      });
    };

    return {goToSeatSelection, planeId};
  },

  data() {
    return {
      menuStart: false,
      menuEnd: false,
      flights: [],
      filters: {
        arrivalCity: "",
        departureCity: "",
        startDate: "",
        endDate: "",
        priceRange: [0, 500],
      },
      minPrice: 0,
      maxPrice: 1000,
    };
  },

  mounted() {
    this.fetchFlights();
    this.updatePriceRange();
  },

  computed: {
    filteredFlights() {
      return this.flights.filter((flight) => {
        if (
            this.filters.arrivalCity &&
            !flight.arrivalCity.toLowerCase().includes(this.filters.arrivalCity.toLowerCase())
        ) {
          return false;
        }
        if (
            this.filters.departureCity &&
            !flight.departureCity.toLowerCase().includes(this.filters.departureCity.toLowerCase())
        ) {
          return false;
        }
        const [minPrice, maxPrice] = this.filters.priceRange;
        if (flight.price < minPrice || flight.price > maxPrice) {
          return false;
        }
        const flightDate = new Date(flight.departOn).setHours(0, 0, 0, 0);
        const startDate = this.filters.startDate ? new Date(this.filters.startDate).setHours(0, 0, 0, 0) : null;
        const endDate = this.filters.endDate ? new Date(this.filters.endDate).setHours(0, 0, 0, 0) : null;

        if (startDate && flightDate < startDate) return false;
        return !(endDate && flightDate > endDate);

      });
    },
  },
  methods: {
    applyFilters() {
      this.filteredFlights;
    },
    async fetchFlights() {
      try {
        const response = await axios.get("/api/flight", {
          params: {planeId: this.planeId},
        });

        this.flights = response.data.map(flight => ({
          ...flight,
          departOn: new Date(flight.departOn),
          arriveOn: new Date(flight.arriveOn),
        }));

        this.updatePriceRange();
      } catch (error) {
        console.error("Error fetching flights:", error);
      }
    },
    calculateDuration(departOn, arriveOn) {
      const departTime = new Date(departOn);
      const arriveTime = new Date(arriveOn);
      const duration = (arriveTime - departTime) / 1000 / 60 / 60;
      return Math.round(duration.toFixed(2));
    },
    formatDepartDate(date) {
      const d = new Date(date);
      const options = {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      };
      return d.toLocaleString('en-GB', options).replace(',', ''); // Saadab kuupäeva nagu 01/04/2025, 09:34:05
    },
    formatArriveDate(date) {
      const d = new Date(date);
      const options = {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      };
      return d.toLocaleString('en-GB', options).replace(',', ''); // Saadab kuupäeva nagu 01/04/2025, 09:34:05
    },
    updatePriceRange() {
      const prices = this.flights.map(flight => flight.price);
      this.minPrice = Math.min(...prices);
      this.maxPrice = Math.max(...prices);
      this.filters.priceRange = [this.minPrice, this.maxPrice];
    },
  },

  watch: {
    'filters.priceRange': function (newValue) {
      this.applyFilters();
    },
  },

};

</script>
<style scoped>
.flight-item {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.filters {
  margin-bottom: 20px;
}

.filters label {
  margin-right: 10px;
}

.filters input {
  margin-right: 20px;
  padding: 5px;
}

.primary {
  background-color: #6FA1FF !important;
}

.v-btn {
  background-color: #6FA1FF;
  color: white;
}

.v-btn:hover {
  background-color: #87BFFF;
}

.v-card {
  background-color: #BEDCFF;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.v-card-title {
  color: #6FA1FF;
}


.v-range-slider {
  color: #A3CDFF;
}

.price-slider {
  margin-top: 20px;
}

.v-row {
  margin-top: 10px;
}

.v-col {
  font-size: 14px;
  color: #6fa1ff;
}

.v-alert {
  background-color: #BEDCFF;
  color: #6FA1FF;
}
</style>
