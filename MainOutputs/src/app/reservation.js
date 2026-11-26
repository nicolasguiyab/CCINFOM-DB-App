
document.addEventListener("DOMContentLoaded", () => {

    // =====================================
    // ELEMENTS
    // =====================================

    const calendarDays = document.getElementById("calendarDays");
    const monthYear = document.getElementById("monthYear");
    prevMonth.addEventListener("click", () => {

        currentDate.setMonth(currentDate.getMonth() - 1);

        loadMonthlyReservations();

    });

    nextMonth.addEventListener("click", () => {

        currentDate.setMonth(currentDate.getMonth() + 1);

        loadMonthlyReservations();

    });

    const reservationDate = document.getElementById("reservationDate");
    const selectedDateDisplay = document.getElementById("selectedDate");

    const customerIDInput = document.getElementById("customerID");
    const customerNameInput = document.getElementById("customerName");

    const branchSelect = document.getElementById("branchID");
    const spaceSelect = document.getElementById("spaceID");

    const startTime = document.getElementById("startTime");
    const endTime = document.getElementById("endTime");

    const hourlyRateDisplay = document.getElementById("hourlyRate");
    const durationDisplay = document.getElementById("duration");
    const totalPriceDisplay = document.getElementById("totalPrice");

    const operatingHoursDisplay =
        document.getElementById("operatingHoursDisplay");

    let branchOpeningTime = "";
    let branchClosingTime = "";

    let currentDate = new Date();
    let selectedDateValue = "";

    let reservationSlots = [];
    let availabilityLoaded = false;

    let monthlyReservations = [];

    // =====================================
    // CUSTOMER INFORMATION
    // =====================================

    async function loadCustomerInformation() {

        const customerID = localStorage.getItem("customerID");

        if (!customerID) {
            alert("Please log in first.");
            window.location.href = "./login.html";
            return;
        }

        try {
            const response = await fetch(
                `http://localhost:8080/api/customer?customerID=${encodeURIComponent(customerID)}`
            );

            if (!response.ok) {
                throw new Error("Unable to retrieve customer.");
            }

            const customer = await response.json();

            customerIDInput.textContent = customer.customerID;
            customerNameInput.textContent = customer.customerName;

        } catch (error) {
            console.error("Customer error:", error);
            alert("Could not load customer information.");
        }
    }

    loadCustomerInformation();

    // =====================================
    // CALENDAR FUNCTIONS
    // =====================================

    function formatDate(year, month, day) {
        return [
            year,
            String(month + 1).padStart(2, "0"),
            String(day).padStart(2, "0")
        ].join("-");
    }

    function getWeekNumber(date) {

        const tempDate = new Date(date);
        tempDate.setHours(0, 0, 0, 0);

        tempDate.setDate(
            tempDate.getDate() + 3 -
            (tempDate.getDay() + 6) % 7
        );

        const firstThursday = new Date(
            tempDate.getFullYear(), 0, 4
        );

        return 1 + Math.round(
            ((tempDate - firstThursday) / 86400000 -
                3 + (firstThursday.getDay() + 6) % 7) / 7
        );
    }


    function generateCalendar() {

        calendarDays.innerHTML = "";

        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        monthYear.textContent = currentDate.toLocaleDateString(
            "en-US",
            {
                month: "long",
                year: "numeric"
            }
        );

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);

        const startingDay = (firstDay.getDay() + 6) % 7;
        const totalDays = lastDay.getDate();

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const totalCells =
            Math.ceil((startingDay + totalDays) / 7) * 7;

        let dayCounter = 1;

        for (let cell = 0; cell < totalCells; cell++) {

            const column = cell % 7;

            if (column === 0) {

                const weekStart = new Date(
                    year, month, 1 - startingDay
                );

                weekStart.setDate(weekStart.getDate() + cell);

                const weekNumber = document.createElement("div");

                weekNumber.classList.add("week-number");
                weekNumber.textContent = getWeekNumber(weekStart);

                calendarDays.appendChild(weekNumber);
            }

            const dayCell = document.createElement("div");

            if (cell < startingDay || dayCounter > totalDays) {

                dayCell.classList.add("empty-date");

            } else {

                const day = dayCounter;
                const dateString = formatDate(year, month, day);

                const cellDate = new Date(year, month, day);
                cellDate.setHours(0, 0, 0, 0);

                dayCell.textContent = day;

                if (cellDate < today) {

                    dayCell.classList.add("past-date");

                } else {

                    // Find reservations for this date
                    const dailyReservations = monthlyReservations.filter(
                        reservation =>
                            reservation.bookingDate === dateString
                    );

                    const hasPaid = dailyReservations.some(
                        reservation =>
                            reservation.paymentStatus === "Paid"
                    );

                    const hasPending = dailyReservations.some(
                        reservation =>
                            reservation.paymentStatus === "Unpaid"
                    );

                    // Determine the calendar color
                    if (hasPaid) {
                        dayCell.classList.add("unavailable");
                    } else if (hasPending) {
                        dayCell.classList.add("limited");
                    } else {
                        dayCell.classList.add("available");
                    }

                    // Allow date selection even if some slots are booked
                    dayCell.addEventListener("click", () => {

                        selectedDateValue = dateString;
                        reservationDate.value = dateString;
                        populateTimeOptions();

                        selectedDateDisplay.textContent =
                            cellDate.toLocaleDateString("en-US", {
                                month: "long",
                                day: "numeric",
                                year: "numeric"
                            });

                        updateTimeRestrictions();

                        generateCalendar();
                    });
                }

                if (dateString === selectedDateValue) {
                    dayCell.classList.add("selected-date");
                }

                dayCounter++;
            }

            calendarDays.appendChild(dayCell);
        }
    }


    prevMonth.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        generateCalendar();
    });

    nextMonth.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        generateCalendar();
    });

    // =====================================
    // TIME VALIDATION AND PRICE CALCULATION
    // =====================================

    function calculateReservationPrice() {

        const selectedDate = reservationDate.value;
        const start = startTime.value;
        const end = endTime.value;

        startTime.setCustomValidity("");
        endTime.setCustomValidity("");

        if (!selectedDate || !start || !end) {
            durationDisplay.textContent = "0 hours";
            totalPriceDisplay.textContent = "₱0.00";
            return;
        }

        const startDateTime = new Date(`${selectedDate}T${start}`);
        const endDateTime = new Date(`${selectedDate}T${end}`);

        if (endDateTime <= startDateTime) {

            endTime.setCustomValidity(
                "End time must be later than start time."
            );

            durationDisplay.textContent = "Invalid time";
            totalPriceDisplay.textContent = "₱0.00";
            return;
        }

        if (startDateTime < new Date()) {

            startTime.setCustomValidity(
                "You cannot reserve a time that has already passed."
            );

            durationDisplay.textContent = "Invalid time";
            totalPriceDisplay.textContent = "₱0.00";
            return;
        }

        // Only allow 30-minute intervals
        const startMinutes =
            Number(start.split(":")[0]) * 60 +
            Number(start.split(":")[1]);

        const endMinutes =
            Number(end.split(":")[0]) * 60 +
            Number(end.split(":")[1]);

        if (startMinutes % 30 !== 0) {
            startTime.setCustomValidity(
                "Start time must be in 30-minute intervals."
            );
            return;
        }

        if (endMinutes % 30 !== 0) {
            endTime.setCustomValidity(
                "End time must be in 30-minute intervals."
            );
            return;
        }

        const duration =
            (endDateTime - startDateTime) / 3600000;

        const hourlyRate = Number(
            hourlyRateDisplay.dataset.rate || 0
        );

        const totalPrice = duration * hourlyRate;

        durationDisplay.textContent =
            `${duration.toFixed(2)} hours`;

        totalPriceDisplay.textContent =
            `₱${totalPrice.toFixed(2)}`;
    }


    function updateTimeRestrictions() {

        const selectedDate = reservationDate.value;

        // No branch selected yet
        if (!branchOpeningTime || !branchClosingTime) {
            startTime.removeAttribute("min");
            startTime.removeAttribute("max");
            endTime.removeAttribute("min");
            endTime.removeAttribute("max");
            return;
        }

        startTime.step = 1800;
        endTime.step = 1800;

        // Start time cannot be before opening or after closing
        let earliestStart = branchOpeningTime;

        const now = new Date();

        const today = formatDate(
            now.getFullYear(),
            now.getMonth(),
            now.getDate()
        );

        // Prevent booking past times when booking today
        if (selectedDate === today) {

            const currentTime =
                `${String(now.getHours()).padStart(2, "0")}:` +
                `${String(now.getMinutes()).padStart(2, "0")}`;

            if (currentTime > earliestStart) {
                earliestStart = currentTime;
            }
        }

        startTime.min = earliestStart;
        startTime.max = branchClosingTime;

        // End time must be later than start time
        endTime.min = startTime.value || branchOpeningTime;
        endTime.max = branchClosingTime;

        // Clear times that are no longer valid
        if (startTime.value &&
            (startTime.value < earliestStart ||
                startTime.value >= branchClosingTime)) {
            startTime.value = "";
        }

        if (endTime.value &&
            (endTime.value <= startTime.value ||
                endTime.value > branchClosingTime)) {
            endTime.value = "";
        }

        calculateReservationPrice();
    }



    startTime.addEventListener("change", () => {

        endTime.innerHTML =
            '<option value="">Select end time</option>';

        endTime.disabled = true;

        if (!startTime.value) return;

        const startMinutes = toTimeMinutes(startTime.value);

        const closingMinutes = toTimeMinutes(branchClosingTime);

        for (
            let minutes = startMinutes + 30;
            minutes <= closingMinutes;
            minutes += 30
        ) {

            const status = getSlotStatus(startMinutes, minutes);

            const option = document.createElement("option");

            option.value = formatMinutes(minutes);

            option.textContent =
                `${getStatusSymbol(status)} ${formatTimeDisplay(minutes)} — ${status.toUpperCase()}`;

            option.dataset.status = status;

            if (status !== "available") {
                option.disabled = true;
            }

            endTime.appendChild(option);
        }

        endTime.disabled = false;

        updateTimeRestrictions();
    });

    endTime.addEventListener("change", calculateReservationPrice);

    // =====================================
    // BRANCH SELECTION
    // =====================================

    async function loadBranches() {

        try {

            const response = await fetch(
                "http://localhost:8080/api/branches"
            );

            if (!response.ok) {
                throw new Error(`Branch API error: ${response.status}`);
            }

            const branches = await response.json();

            branchSelect.innerHTML =
                '<option value="">Choose a branch</option>';

            branches.forEach(branch => {

                const option = document.createElement("option");

                option.value = branch.branchID;
                option.textContent =
                    `${branch.branchName} - ${branch.city}`;

                branchSelect.appendChild(option);
            });

            if (branches.length === 0) {
                branchSelect.innerHTML =
                    '<option value="">No branches found</option>';
            }

            console.log("Branches loaded:", branches);

        } catch (error) {

            console.error("Branch loading error:", error);

            branchSelect.innerHTML =
                '<option value="">Unable to load branches</option>';
        }
    }

    // =====================================
    // STUDY SPACE SELECTION
    // =====================================

    async function loadStudySpaces(branchID) {

        spaceSelect.innerHTML =
            '<option value="">Loading study spaces...</option>';

        spaceSelect.disabled = true;

        hourlyRateDisplay.dataset.rate = "0";
        hourlyRateDisplay.textContent = "₱0.00";

        calculateReservationPrice();

        try {

            const response = await fetch(
                `http://localhost:8080/api/study-spaces?branchID=${encodeURIComponent(branchID)}`
            );

            if (!response.ok) {
                throw new Error(`Study space API error: ${response.status}`);
            }

            const spaces = await response.json();

            spaceSelect.innerHTML =
                '<option value="">Choose a study space</option>';

            const availableSpaces = spaces.filter(space =>
                String(space.spaceStatus).toLowerCase() === "available"
            );

            availableSpaces.forEach(space => {

                const option = document.createElement("option");

                option.value = space.spaceID;

                option.textContent =
                    `${space.spaceType} - Capacity: ${space.capacity}`;

                option.dataset.rate = space.hourlyRate;

                spaceSelect.appendChild(option);
            });

            if (availableSpaces.length === 0) {

                spaceSelect.innerHTML =
                    '<option value="">No available study spaces</option>';

                return;
            }

            spaceSelect.disabled = false;

            console.log("Study spaces loaded:", availableSpaces);

        } catch (error) {

            console.error("Study space loading error:", error);

            spaceSelect.innerHTML =
                '<option value="">Unable to load study spaces</option>';
        }
    }


    async function loadOperatingHours(branchID) {

        try {

            const response = await fetch(
                `http://localhost:8080/api/operating-hours?branchID=${encodeURIComponent(branchID)}`
            );

            if (!response.ok) {
                throw new Error("Unable to retrieve operating hours.");
            }

            const hours = await response.json();

            branchOpeningTime = hours.openingTime;
            branchClosingTime = hours.closingTime;

            populateTimeOptions();

            operatingHoursDisplay.textContent =
                `Operating Hours: ${branchOpeningTime} – ${branchClosingTime}`;

            updateTimeRestrictions();

        } catch (error) {

            console.error("Operating hours error:", error);

            branchOpeningTime = "";
            branchClosingTime = "";

            operatingHoursDisplay.textContent =
                "Unable to retrieve operating hours.";

            startTime.removeAttribute("min");
            endTime.removeAttribute("max");

            alert("Could not load the branch operating hours.");
        }
    }


    async function loadMonthlyReservations() {

        const spaceID = spaceSelect.value;

        monthlyReservations = [];

        if (!spaceID) {
            generateCalendar();
            return;
        }

        try {

            const month = currentDate.getMonth() + 1;
            const year = currentDate.getFullYear();

            const response = await fetch(
                `http://localhost:8080/api/monthly-reservations?spaceID=${encodeURIComponent(spaceID)}&month=${month}&year=${year}`
            );

            if (!response.ok) {
                throw new Error("Unable to retrieve monthly reservations.");
            }

            monthlyReservations = await response.json();

            console.log("Monthly reservations:", monthlyReservations);

            generateCalendar();

        } catch (error) {

            console.error("Monthly reservations error:", error);

            monthlyReservations = [];

            alert("Unable to load the reservation calendar.");
        }
    }



    branchSelect.addEventListener("change", () => {

        const branchID = branchSelect.value;

        spaceSelect.innerHTML =
            '<option value="">Choose a branch first</option>';

        spaceSelect.disabled = true;

        hourlyRateDisplay.dataset.rate = "0";
        hourlyRateDisplay.textContent = "₱0.00";

        calculateReservationPrice();

        if (branchID) {
            loadStudySpaces(branchID);
            loadOperatingHours(branchID);
        } else {
            branchOpeningTime = "";
            branchClosingTime = "";

            operatingHoursDisplay.textContent =
                "Select a branch to view operating hours.";
        }

        monthlyReservations = [];
        generateCalendar();
    });

    spaceSelect.addEventListener("change", () => {

        const selectedOption =
            spaceSelect.options[spaceSelect.selectedIndex];

        const rate = Number(selectedOption.dataset.rate || 0);

        hourlyRateDisplay.dataset.rate = rate;
        hourlyRateDisplay.textContent = `₱${rate.toFixed(2)}`;

        calculateReservationPrice();
        loadMonthlyReservations();
    });

    // =====================================
    // INITIALIZE PAGE
    // =====================================

    generateCalendar();

    // ================================
// SUBMIT RESERVATION
// ================================

    const reservationForm = document.getElementById("reservationForm");
    const confirmButton = document.getElementById("confirmReservation");

    reservationForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        // Check required fields
        if (!reservationForm.checkValidity()) {
            reservationForm.reportValidity();
            return;
        }

        // Make sure a study space is selected
        if (!spaceSelect.value) {
            alert("Please select a study space.");
            return;
        }

        // Make sure the date and time are valid
        calculateReservationPrice();

        if (!reservationForm.checkValidity()) {
            reservationForm.reportValidity();
            return;
        }

        const reservationData = {
            customerID: localStorage.getItem("customerID"),
            spaceID: spaceSelect.value,
            reservationDate: reservationDate.value,
            startTime: startTime.value,
            endTime: endTime.value
        };

        try {

            confirmButton.disabled = true;
            confirmButton.textContent = "Processing...";

            const response = await fetch(
                "http://localhost:8080/api/reservations",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: new URLSearchParams(reservationData)
                }
            );

            const message = await response.text();

            if (!response.ok) {
                throw new Error(message);
            }

            alert(message);

            // Reset reservation selections
            branchSelect.value = "";

            spaceSelect.innerHTML =
                '<option value="">Choose a branch first</option>';

            spaceSelect.disabled = true;

            reservationForm.reset();

            hourlyRateDisplay.dataset.rate = "0";
            hourlyRateDisplay.textContent = "₱0.00";

            durationDisplay.textContent = "0 hours";
            totalPriceDisplay.textContent = "₱0.00";

            selectedDate.textContent = "None";

            // Restore the logged-in customer's information
            await loadCustomerInformation();

            generateCalendar();

        } catch (error) {

            console.error("Reservation error:", error);

            alert(error.message || "Unable to create reservation.");

        } finally {

            confirmButton.disabled = false;
            confirmButton.textContent = "Confirm Reservation";

        }

    });


    function populateTimeOptions() {

        startTime.innerHTML =
            '<option value="">Select start time</option>';

        endTime.innerHTML =
            '<option value="">Select end time</option>';

        endTime.disabled = true;

        if (!branchOpeningTime || !branchClosingTime) {
            return;
        }

        const toMinutes = time => {
            const [hours, minutes] = time.split(":").map(Number);
            return hours * 60 + minutes;
        };

        const formatTime = minutes => {

            const hours = Math.floor(minutes / 60);
            const mins = minutes % 60;

            const date = new Date();
            date.setHours(hours, mins, 0, 0);

            return date.toLocaleTimeString("en-US", {
                hour: "numeric",
                minute: "2-digit"
            });
        };

        const opening = toMinutes(branchOpeningTime);
        const closing = toMinutes(branchClosingTime);

        const now = new Date();

        const today = formatDate(
            now.getFullYear(),
            now.getMonth(),
            now.getDate()
        );

        let earliest = opening;

        if (reservationDate.value === today) {

            const currentMinutes =
                now.getHours() * 60 + now.getMinutes();

            earliest = Math.max(
                opening,
                Math.ceil(currentMinutes / 30) * 30
            );
        }

        for (
            let minutes = opening;
            minutes + 30 <= closing;
            minutes += 30
        ) {

            if (minutes < earliest) continue;

            const status = getSlotStatus(minutes, minutes + 30);

            const option = document.createElement("option");

            option.value = formatMinutes(minutes);

            option.textContent =
                `${getStatusSymbol(status)} ${formatTime(minutes)} — ${status.toUpperCase()}`;

            option.dataset.status = status;

            if (status !== "available") {
                option.disabled = true;
            }

            startTime.appendChild(option);
        }
    }



    async function loadReservationSlots() {

        const spaceID = spaceSelect.value;
        const bookingDate = reservationDate.value;

        reservationSlots = [];
        availabilityLoaded = false;

        if (!spaceID || !bookingDate) {
            generateCalendar();
            return;
        }

        try {

            const response = await fetch(
                `http://localhost:8080/api/reservation-slots?spaceID=${encodeURIComponent(spaceID)}&bookingDate=${encodeURIComponent(bookingDate)}`
            );

            if (!response.ok) {
                throw new Error("Unable to retrieve reservation availability.");
            }

            reservationSlots = await response.json();
            availabilityLoaded = true;

            generateCalendar();

        } catch (error) {

            console.error("Availability error:", error);

            availabilityLoaded = false;

            alert("Unable to load reservation availability.");
        }
    }


    function getSlotStatus(startMinutes, endMinutes) {

        const selectedDate = reservationDate.value;

        const overlappingReservations = monthlyReservations.filter(
            reservation => {

                if (reservation.bookingDate !== selectedDate) {
                    return false;
                }

                const [startHour, startMinute] =
                    reservation.startTime.split(":").map(Number);

                const [endHour, endMinute] =
                    reservation.endTime.split(":").map(Number);

                const reservedStart = startHour * 60 + startMinute;
                const reservedEnd = endHour * 60 + endMinute;

                return startMinutes < reservedEnd &&
                    endMinutes > reservedStart;
            }
        );

        if (overlappingReservations.some(
            reservation => reservation.paymentStatus === "Paid"
        )) {
            return "paid";
        }

        if (overlappingReservations.some(
            reservation => reservation.paymentStatus === "Unpaid"
        )) {
            return "pending";
        }

        return "available";
    }


    function formatMinutes(minutes) {

        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;

        return `${String(hours).padStart(2, "0")}:${String(mins).padStart(2, "0")}`;
    }

    function getStatusSymbol(status) {

        if (status === "paid") return "🟥";
        if (status === "pending") return "🟧";

        return "⬜";
    }

    function toTimeMinutes(time) {
        const [hours, minutes] = time.split(":").map(Number);
        return hours * 60 + minutes;
    }

    function formatTimeDisplay(minutes) {
        const date = new Date();
        date.setHours(Math.floor(minutes / 60), minutes % 60, 0, 0);

        return date.toLocaleTimeString("en-US", {
            hour: "numeric",
            minute: "2-digit"
        });
    }




    loadBranches();

});


