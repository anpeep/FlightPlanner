<template>
  <div class="side-panel">
    <h3>Filters</h3>

    <div class="filter">
      <input type="checkbox" id="window" v-model="filters.window" />
      <label for="window">Window Seat</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="exit" v-model="filters.exit" />
      <label for="exit">Near Exit</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="legroom" v-model="filters.legroom" />
      <label for="legroom">More Legroom</label>
    </div>

    <button class="apply-filters-btn" @click="applyFilters">Apply Filters</button>
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
        legroom: false
      }
    };
  },
  methods: {
    async applyFilters() {
      try {
        const selectedFilters = Object.keys(this.filters)
            .filter(key => this.filters[key])
            .map(filter => filter.toUpperCase());

        const response = await axios.post("/api/seats/filters", null, {
          params: {
            flightId: this.$route.query.flightId,
            planeId: this.$route.query.planeId,
            filters: selectedFilters
          }
        });

        this.$emit("updateSeats", response.data);
      } catch (error) {
        console.error("Error applying filters:", error);
      }
    }
  }
};
</script>
