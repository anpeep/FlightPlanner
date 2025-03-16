export function getFlightDuration(departureTime, arrivalTime) {
    const departDate = new Date(departureTime);
    const arriveDate = new Date(arrivalTime);

    const durationInMinutes = (arriveDate - departDate) / 60000; // Arvutab kestuse minutites
    const hours = Math.floor(durationInMinutes / 60); // Tunnid
    const minutes = durationInMinutes % 60; // Minutid

    return `${hours}h ${minutes}m`;
}
export function increaseSeatCount(seatCount, updateLocalStorage, applyFilters) {
    if (seatCount < 72) {
        seatCount++;
        updateLocalStorage(seatCount);  // Update localStorage with new seat count
        applyFilters();  // Apply any additional filter logic if needed
    }
    return seatCount;
}

export function decreaseSeatCount(seatCount, updateLocalStorage, applyFilters) {
    if (seatCount > 1) {
        seatCount--;
        updateLocalStorage(seatCount);  // Update localStorage with new seat count
        applyFilters();  // Apply any additional filter logic if needed
    }
    return seatCount;
}

export function updateLocalStorage(seatCount) {
    localStorage.setItem("seatCount", seatCount); // Update seat count in localStorage
}
