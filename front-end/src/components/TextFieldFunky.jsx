import React from 'react';
import { TextField } from '@mui/material';

const TextFieldFunky = ({ sx, ...props }) => {
    return (
        <TextField
            fullWidth
            variant="outlined"
            sx={{

                    '&.Mui-focused fieldset': {
                        borderColor: '#ffb15a', // Mėlynas apvadas, kai focus
                    },
                ...sx, // Leidžia pernaudoti stilius
            }}
            {...props} // Leidžia praeiti papildomus props
        />
    );
};

export default TextFieldFunky;