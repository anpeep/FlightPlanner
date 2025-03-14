<template>
  <div class="side-panel">
    <h3>Filters</h3>

    <div class="filter">
      <input type="checkbox" id="window" v-model="filters.window" @change="applyFilters" />
      <label for="window">Window Seat</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="exit" v-model="filters.exit" @change="applyFilters" />
      <label for="exit">Near Exit</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="legroom" v-model="filters.legroom" @change="applyFilters" />
      <label for="legroom">More Legroom</label>
    </div>
    <div class="filter">
      <input type="checkbox" id="near" v-model="filters.near" @change="applyFilters" />
      <label for="near">Near Seats</label>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      filters: {
        window: false,
        exit: false,
        legroom: false,
        near: false,
      },

      seats: [],
    };
  },
  methods: {
      async applyFilters() {
        try {
          const filters = [];
          if (this.filters.window) filters.push(1);
          if (this.filters.exit) filters.push(2);
          if (this.filters.legroom) filters.push(3);
          if (this.filters.near) filters.push(4);
          const flightId = this.$route.query.flightId;
          const planeId = this.$route.query.planeId;
          const seatCount = localStorage.getItem('ticketCount');
          const response = await axios.post("/api/seats/addFilters", filters, {
            params: {
              seatCount: seatCount,
              flightId: flightId,
              planeId: planeId
            }
          });
          this.$emit("filtersUpdated", response.data);
        } catch (error) {
          console.error("❌ Error applying filters:", error.response?.data || error.message);
        }

    }
  },
};
</script>
