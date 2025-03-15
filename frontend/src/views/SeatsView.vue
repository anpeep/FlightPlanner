<template>
  <div class="seating-view">
    <RecsPanel @filtersUpdated="updateSeats" />
    <div class="seating">
      <div v-for="row in seats" :key="row.row" class="row">
        <div class="seat-group">
          <Seat
              v-for="seat in row.seats"
              :key="row.row + seat.position"
              :number="row.row + seat.position"
              :isBooked="seat.booked"
              :isRecommended="seat.recommended"
              classType="seat-item"
              @swapSeats="swapSeats"
          />
        </div>
      </div>
    </div>


  </div>
</template>

<script>
import axios from "axios";
import Seat from "@/components/Seat.vue";
import RecsPanel from "@/components/RecsPanel.vue";

export default {
  components: { Seat, RecsPanel },
  data() {
    return {
      seats: [],
      bookedSeats: [],
      recommendedSeats: [],
      availableSeats: [],
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
            planeId: this.$route.query.planeId,
          },
        });
        console.log(response.data);
        this.availableSeats = response.data.availableSeats;
        this.bookedSeats = response.data.bookedSeats;
        console.log(this.bookedSeats + "mama");
        this.recommendedSeats = response.data.recommendedSeats;
        this.generateSeats();
      } catch (error) {
        console.error("❌ Error loading seats:", error);
      }
    },
    updateSeats(filteredSeats) {
      this.availableSeats = filteredSeats.filter(seat => !seat.booked && !seat.recommended);
      this.recommendedSeats = filteredSeats.filter(seat => seat.recommended);
      this.generateSeats();
    },
    generateSeats() {
      const seatMap = new Map(this.bookedSeats.map(seat => [`${seat.row}${seat.seat_column}`, true]));
      const recommendedSeatMap = new Map(this.recommendedSeats.map(seat => [`${seat.row}${seat.seat_column}`, true])); // Muuda väärtuseks true
      const totalRows = 11;

      this.seats = Array.from({ length: totalRows }, (_, rowIndex) => {
        const row = rowIndex + 1;
        let seatPositions;

        if (row === 1) {
          seatPositions = ["C", "D", "E", "F"];
        } else if (row === 5 || row === 11) {
          seatPositions = ["D", "E"];
        } else {
          seatPositions = ["A", "B", "C", "D", "E", "F", "G", "H"];
        }
        console.log(seatMap + "sm")
        console.log(recommendedSeatMap + "rm")

        return {
          row,
          seats: seatPositions.map(pos => {
            const key = `${row}${pos}`;
            return {
              position: pos,
              booked: seatMap.has(key),
              recommended: recommendedSeatMap.has(key),
            }
          })
        };
      });
    },

    swapSeats(fromSeat, toSeat) {
      let fromSeatObj = null;
      let toSeatObj = null;
      for (const row of this.seats) {
        for (const seat of row.seats) {
          const seatKey = `${row.row}${seat.position}`;
          if (seatKey === fromSeat) fromSeatObj = seat;
          if (seatKey === toSeat) toSeatObj = seat;
        }
      }

      if (fromSeatObj.recommended && !toSeatObj.recommended && !toSeatObj.booked) {
        fromSeatObj.recommended = false;
        toSeatObj.recommended = true;
      }
    }
  },
};
</script>
<style scoped>

.airplane-body {
  top: 30vh;
  position: absolute;
  right: 18vw;  /* Paigutab lennuki keha paremasse nurka */
  transform: translate(50%, -50%); /* Korrigeerib keha positsiooni täpselt */
}

.airplane-image {
  width: 100%;
  max-width: 40vw;
}

.row {
  display: flex;
  justify-content: center;
}


</style>
