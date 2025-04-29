import React, { useState } from 'react';
import TextField from '@mui/material/TextField';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import nominatim from 'nominatim-client';

const client = nominatim.createClient({
    useragent: 'YourAppName',
    referer: 'http://localhost:5173/',
});

const AddressAutocomplete = ({ value, onSelect }) => {
    const [suggestions, setSuggestions] = useState([]);

    const handleChange = async (e) => {
        const query = e.target.value;
        if (query.length < 3) {
            setSuggestions([]);
            return;
        }

        try {
            const response = await fetch(
                `https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&countrycodes=lt&limit=5&q=${encodeURIComponent(
                    query
                )}`,
                {
                    headers: {
                        'Accept': 'application/json',
                    }
                }
            );
            const data = await response.json();
            setSuggestions(data);
        } catch (error) {
            console.error('Error fetching address suggestions:', error);
        }
    };

    return (
        <div>
            <TextField
                fullWidth
                label="Adresas"
                variant="outlined"
                value={value}
                onChange={handleChange}
            />
            <List>
                {suggestions.map((suggestion, index) => (
                    <ListItem
                        button
                        key={index}
                        onClick={() => onSelect(suggestion.display_name)}
                    >
                        {suggestion.display_name}
                    </ListItem>
                ))}
            </List>
        </div>
    );
};

export default AddressAutocomplete;