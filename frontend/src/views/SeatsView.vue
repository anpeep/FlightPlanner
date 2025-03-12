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
              :isRecommended="seat.recommended"
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
      seats: [], // Toolide andmed
      bookedSeats: [], // Broneeritud toolid
      recommendedSeats: []
    };
  },
  async created() {
    await this.loadSeats();
  },
  methods: {
    async loadSeats() {
      try {
        const response = await axios.get("/api/seats/getSeatsByFlight", {
          params: {
            flightId: this.$route.query.flightId,
            planeId: this.$route.query.planeId
          }
        });

        this.availableSeats = response.data.availableSeats;
        this.bookedSeats = response.data.bookedSeats;
        this.recommendedSeats = response.data.recommendedSeats;

        console.log("Available Seats: ", this.availableSeats);
        console.log("Booked Seats: ", this.bookedSeats);
        console.log("Recommended Seats: ", this.recommendedSeats);

        this.generateSeats();
      } catch (error) {
        console.error("Error loading seats:", error);
      }
    },

    generateSeats() {
      const seatMap = new Map(this.bookedSeats.map(seat => [`${seat.row}${seat.seat_column}`, seat.available]));
      const recommendedSeatMap = new Map(this.recommendedSeats.map(seat => [`${seat.row}${seat.seat_column}`, true])); // Muuda väärtuseks true

      this.seats = Array.from({length: 11}, (_, index) => {
        const row = index + 1;
        let seatPositions;

        if (row === 1) {
          seatPositions = ["1A", "B", "E", "F"];
        } else if (row === 5 || row === 11) {
          seatPositions = ["A", "F"];
        } else {
          seatPositions = ["A", "B", "C", "D", "E", "F", "G", "H"];
        }

        return {
          row,
          seats: seatPositions.map(pos => {
            const key = `${row}${pos}`;
            return {
              position: pos,
              booked: seatMap.has(key),
              recommended: recommendedSeatMap.has(key)
            };
          })
        };
      });

      console.log("Generated Seats:", this.seats);
    }
  }
  };
</script>
