import React from 'react';
import { Button } from '@mui/material';

const ButtonFunky = ({ onClick, children, sx, ...props }) => {
    return (
        <Button
            variant="contained"
            onClick={onClick}
            sx={{
                backgroundColor: '#ffb15a', // Šviesus oranžinis atspalvis
                border: '2px solid #422800', // Tamsesnis rudas apvadas
                borderRadius: '30px', // Apvalūs kampai
                boxShadow: '4px 4px 0 0 #422800', // Šviesus rudas šešėlis
                color: '#422800', // Tamsiai ruda spalva
                cursor: 'pointer',
                display: 'inline-block',
                fontWeight: 600,
                fontSize: '18px',
                padding: '0 18px',
                lineHeight: '50px',
                textAlign: 'center',
                textDecoration: 'none',
                userSelect: 'none',
                touchAction: 'manipulation',

                '&:hover': {
                    backgroundColor: '#fff', // Baltas fonas, kai paspaudžiamas
                },
                '&:active': {
                    boxShadow: '2px 2px 0 0 #422800', // Tamsesnis šešėlis aktyvumui
                    transform: 'translate(2px, 2px)', // Šiek tiek perkeliamas aktyvumui
                },
                '@media (min-width: 768px)': {
                    minWidth: '120px', // Minimalus plotis didesniems ekranams
                    padding: '0 25px', // Didesnis padding didesniuose ekranuose
                },
                ...sx, // Leidžia pernaudoti stilius
            }}
            {...props}
        >
            {children}
        </Button>
    );
};

export default  ButtonFunky;