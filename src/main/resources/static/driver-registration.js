document.addEventListener('DOMContentLoaded', () => {
    const citySelect = document.getElementById('cityName');
    const form = document.getElementById('driver-registration-form');
    const responseDiv = document.getElementById('response');
    const loadingSpinner = document.getElementById('loading-spinner');

    // Функция для загрузки городов
    async function loadCities() {
        try {
            loadingSpinner.style.display = 'block'; // Показываем индикатор загрузки
            const response = await fetch('/api/cities');
            if (!response.ok) {
                throw new Error(`Failed to fetch cities: ${response.statusText}`);
            }

            const cities = await response.json();
            citySelect.innerHTML = '<option value="" disabled selected>Select a city</option>';

            cities.forEach(city => {
                const option = document.createElement('option');
                option.value = city.id;
                option.textContent = city.name;
                citySelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading cities:', error);
            responseDiv.innerHTML = `<p style="color: red;">Failed to load cities: ${error.message}</p>`;
        } finally {
            loadingSpinner.style.display = 'none'; // Скрываем индикатор загрузки
        }
    }

    // Функция для отправки данных о водителе
    async function registerDriver(event) {
        event.preventDefault();
        const name = document.getElementById('name').value;
        const secondName = document.getElementById('secondName').value;
        const password = document.getElementById('password').value;
        const personNumber = document.getElementById('personNumber').value;
        const cityId = citySelect.value;

        if (!cityId) {
            responseDiv.innerHTML = `<p style="color: red;">Please select a city.</p>`;
            return;
        }

        const driverRegistrationDto = {
            id: null,
            name,
            secondName,
            password,
            personNumber,
            cityId
        };

        try {
            loadingSpinner.style.display = 'block'; // Показываем индикатор загрузки
            const response = await fetch('/api/drivers', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(driverRegistrationDto)
            });

            if (response.ok) {
                const result = await response.json();
                responseDiv.innerHTML = `<p class="success">Driver registered successfully: ${JSON.stringify(result)}</p>`;
                form.reset();

                // Редирект на страницу успеха
                window.location.href = '/driver/success';
            } else {
                const error = await response.text();
                responseDiv.innerHTML = `<p style="color: red;">Error: ${error}</p>`;
            }
        } catch (error) {
            console.error('Error registering driver:', error);
            responseDiv.innerHTML = `<p style="color: red;">Request failed: ${error.message}</p>`;
        } finally {
            loadingSpinner.style.display = 'none'; // Скрываем индикатор загрузки
        }
    }

    // Загружаем города при загрузке страницы
    loadCities();

    // Устанавливаем обработчик события для отправки формы
    form.addEventListener('submit', registerDriver);
});



