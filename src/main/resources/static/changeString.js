document.getElementById('changeStringForm').addEventListener('submit', function(event) {
            event.preventDefault();

            var probaValue = document.getElementById('probaInput').value;

            var xhr = new XMLHttpRequest();
            xhr.open('POST', '/index', true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.send(JSON.stringify({ proba: probaValue }));

            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        console.log('String changed successfully!');
                        // Itt hajtsd végre a további teendőket, pl. frissítés az oldalon
                    } else {
                        console.error('Error occurred while changing string!');
                    }
                }
            };
        });