<template>

</template>

<script>

export default {
  props: ["modelValue", "availableDates"],
  data() {
    return {
      currentView: "month", // Algselt kuuvaade
      selectedMonth: null, // Valitud kuu
    };
  },
  computed: {
    calendarAttributes() {
      if (!this.selectedMonth) {
        // Esialgu näitab ainult 12 kuu vaadet
        const monthsWithFlights = [...new Set(this.availableDates.map(date => date.slice(0, 7)))];

        return monthsWithFlights.map(month => ({
          date: month + "-01",
          class: "available-flight"
        }));
      } else {
        // Kui kuu on valitud, näitab selle kuu kõiki päevi
        return this.availableDates.map(date => ({
          date,
          class: "available-flight"
        }));
      }
    }
  },
  methods: {
    handleClick(day) {
      const clickedMonth = day.date.slice(0, 7); // YYYY-MM

      if (!this.selectedMonth) {
        // Kui algses vaates klõpsatakse kuule, lülita päevavaatele
        this.selectedMonth = clickedMonth;
        this.currentView = "month"; // Jääb kuuvaatele, kuid nüüd on päevad nähtavad
      }
    }
  }
};
</script>

<style>
.calendar-container {
  max-width: 500px;
  margin: auto;
}

.custom-calendar {
  border: 1px solid #ddd;
  border-radius: 8px;
}

.available-flight {
  background-color: #4caf50 !important;
  color: white !important;
  font-weight: bold;
  border-radius: 5px;
}
</style>
