<template>
  <div class="seating-view">
    <SidePanel />
    <div class="seating">
      <div v-for="row in seats" :key="row.row" class="row">
        <div class="seat-group">
          <Seat
              v-for="seat in row.seats"
              :key="seat.column + row.row"
              :number="seat.column + row.row"
              :isBooked="seat.booked"
              :classType="seat.type"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Seat from "@/components/Seat.vue";
import SidePanel from "@/components/SidePanel.vue";

export default {
  components: { Seat, SidePanel },
  data() {
    return {
      seats: [],
      bookedSeats: []
    };
  },
  async created() {
    await this.loadSeats();
  },
  methods: {
    async loadSeats() {
      try {
        // Küsi broneeritud kohad serverist
        const response = await axios.get("/api/seats");
        this.bookedSeats = response.data;
        // Genereeri kõik istmed ja märgi broneeritud
        this.generateSeats();
      } catch (error) {
        console.error("Error loading booked seats:", error);
      }
    },
    generateSeats() {
      const seatMap = new Map(this.bookedSeats.map(seat => [`${seat.row}${seat.column}`, true]));

      this.seats = Array.from({ length: 11 }, (_, index) => {
        const row = index + 1;
        let seatPositions = [];

        if (row === 1) {
          seatPositions = ["1A", "B", "E", "F"];
        } else if (row === 5 || row === 11) {
          seatPositions = ["A", "F"];
        } else {
          seatPositions = ["A", "B", "C", "D", "E", "F", "G", "H"];
        }

        return {
          row,
          seats: seatPositions.map(pos => ({
            position: pos,
            booked: seatMap.has(`${row}${pos}`)
          }))
        };
      });
    }
  }
};
</script>
