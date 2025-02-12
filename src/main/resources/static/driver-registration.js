document.addEventListener('DOMContentLoaded', () => {
    const citySelect = document.getElementById('currentCityName');
    const form = document.getElementById('driver-registration-form');
    const responseDiv = document.getElementById('response');
    const loadingSpinner = document.getElementById('loading-spinner');

    async function loadCities() {
        try {
            loadingSpinner.style.display = 'block';
            const response = await fetch('/api/v1/cities');
            if (!response.ok) {
                throw new Error(`Failed to fetch cities: ${response.statusText}`);
            }
            const cities = await response.json();
            citySelect.innerHTML = '<option value="" disabled selected>Select a city</option>';
            cities.forEach(city => {
                const option = document.createElement('option');
                option.value = city.name;
                option.textContent = city.name;
                citySelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading cities:', error);
            responseDiv.innerHTML = `<p style="color: red;">Failed to load cities: ${error.message}</p>`;
        } finally {
            loadingSpinner.style.display = 'none';
        }
    }

    async function registerDriver(event) {
        event.preventDefault();
        const driverRegistrationDto = {
            type: "driverRegistrationDto",
            userName: document.getElementById('userName').value,
            password: document.getElementById('password').value,
            email: document.getElementById('email').value,
            name: document.getElementById('name').value,
            secondName: document.getElementById('secondName').value,
            personNumber: document.getElementById('personNumber').value,
            currentCityName: citySelect.value
        };

        if (!driverRegistrationDto.currentCityName) {
            responseDiv.innerHTML = `<p style="color: red;">Please select a city.</p>`;
            return;
        }

        try {
            loadingSpinner.style.display = 'block';
            const response = await fetch('/api/v1/drivers', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(driverRegistrationDto)
            });

            if (response.ok) {
                const result = await response.json();
                responseDiv.innerHTML = `<p class="success">Driver registered successfully: ${JSON.stringify(result)}</p>`;
                form.reset();
                window.location.href = '/drivers/registration/success';
            } else {
                const error = await response.text();
                responseDiv.innerHTML = `<p style="color: red;">Error: ${error}</p>`;
            }
        } catch (error) {
            console.error('Error registering driver:', error);
            responseDiv.innerHTML = `<p style="color: red;">Request failed: ${error.message}</p>`;
        } finally {
            loadingSpinner.style.display = 'none';
        }
    }

    loadCities();
    form.addEventListener('submit', registerDriver);
});


