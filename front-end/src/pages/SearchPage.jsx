import React, { useEffect, useState } from 'react';
import {useSearchParams} from 'react-router-dom';
import axios from 'axios';
import ActivityCard from '../components/ActivityCard';
import { useAuth } from "../context/AuthContext.jsx";
import {Box, Container, Typography} from "@mui/material";
import { Grid } from "@mui/system";
import FunkyInput from "../components/FunkyInput.jsx";
import 'leaflet/dist/leaflet.css';
import FunkySelect from "../components/FunkySelect.jsx";

const SearchPage = () => {
    const [searchParams] = useSearchParams();
    const query = searchParams.get('query') || '';
    const { token } = useAuth();

    const [topPicks, setTopPicks] = useState([]);
    const [otherResults, setOtherResults] = useState([]);
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [location, setLocation] = useState('');
    const [category, setCategory] = useState('');
    const [minDuration, setMinDuration] = useState('');
    const [maxDuration, setMaxDuration] = useState('');
    const [priceType, setPriceType] = useState('');
    const [deliveryMethod, setDeliveryMethod] = useState('');
    const [scrolled, setScrolled] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            if (window.scrollY > 50) {
                setScrolled(true);
            } else {
                setScrolled(false);
            }
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    useEffect(() => {
        // Build query parameters
        const params = new URLSearchParams({
            query: query.trim() || '', // If query is empty, fetch all activities
            minPrice,
            maxPrice,
            location,
            category,
            minDuration,
            maxDuration,
            priceType,
            deliveryMethod
        });

        const apiUrl = query.trim() ? `/api/activities/search?${params.toString()}` : `/api/activities/all-filtered?${params.toString()}`;

        axios.get(apiUrl, {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then(res => {
                if (Array.isArray(res.data)) {
                    // If fetching all activities, set them in `otherResults`
                    setOtherResults(res.data);
                } else if (res.data.topPicks && res.data.otherResults) {
                    // If using search query, set top picks and other results
                    setTopPicks(res.data.topPicks);
                    setOtherResults(res.data.otherResults);
                } else {
                    console.log("Atsakymo struktūra klaidinga.");
                }
            })
            .catch(error => {
                console.error("Error fetching activities:", error);
            });
    }, [query, token, minPrice, maxPrice, location, category, minDuration, maxDuration, priceType, deliveryMethod]);





    return (
        <Container>
            <Box sx={{ mb: 4 }}>

                <Box sx={{
                    backgroundColor: 'white',
                    paddingBottom: 2,
                    borderBottom: '1px solid #eee',
                    width: '100%',
                    mt: 5
                }}>
                    <Box sx={{
                        display: 'flex',
                        justifyContent: 'center',
                        gap: 2,
                    }}>
                        <FunkyInput
                            label="Min. Kaina"
                            type="number"
                            value={minPrice}
                            onChange={(e) => setMinPrice(e.target.value)}
                            size="small"
                            sx={{ width: { xs: '100%', sm: 'auto' } }} // Pilnas plotis mažesniuose ekranuose
                        />
                        <FunkyInput
                            label="Max. Kaina"
                            type="number"
                            value={maxPrice}
                            onChange={(e) => setMaxPrice(e.target.value)}
                            size="small"
                            sx={{ width: { xs: '100%', sm: 'auto' } }}
                        />
                        <FunkyInput
                            label="Min. Trukmė (min)"
                            type="number"
                            value={minDuration}
                            onChange={(e) => setMinDuration(e.target.value)}
                            size="small"
                            sx={{ width: { xs: '100%', sm: 'auto' } }}
                        />
                        <FunkyInput
                            label="Max. Trukmė (min)"
                            type="number"
                            value={maxDuration}
                            onChange={(e) => setMaxDuration(e.target.value)}
                            size="small"
                            sx={{ width: { xs: '100%', sm: 'auto' } }}
                        />
                        <FunkyInput
                            label="Lokacija"
                            value={location}
                            onChange={(e) => setLocation(e.target.value)}
                            size="small"
                            sx={{ width: { xs: '100%', sm: 'auto' } }}
                        />
                    </Box>

                    <Box sx={{
                        display: 'flex',
                        justifyContent: 'center',
                        gap: 2,
                        flexWrap: 'wrap',
                        alignItems: 'center',  // Panaikins tuščią vietą
                    }}>
                        <FunkySelect
                            label="Kategorija"
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                            options={[
                                { label: 'Visos', value: '' },
                                { label: 'Programavimas', value: 'CODING' },
                                { label: 'Menas', value: 'ART' },
                                { label: 'Muzika', value: 'MUSIC' },
                                { label: 'Sportas', value: 'SPORTS' },
                                { label: 'Mokslas', value: 'SCIENCE' },
                                { label: 'Šokiai', value: 'DANCE' }
                            ]}
                            size="small"
                            sx={{ minWidth: 150 }}
                        />
                        <FunkySelect
                            label="Kainos tipas"
                            value={priceType}
                            onChange={(e) => setPriceType(e.target.value)}
                            options={[
                                { label: 'Visi', value: '' },
                                { label: 'Savaitinis', value: 'WEEKLY' },
                                { label: 'Mėnesinis', value: 'MONTHLY' },
                                { label: 'Nemokamas', value: 'FREE' }
                            ]}
                            size="small"
                            sx={{ minWidth: 150 }}
                        />
                        <FunkySelect
                            label="Būdas"
                            value={deliveryMethod}
                            onChange={(e) => setDeliveryMethod(e.target.value)}
                            options={[
                                { label: 'Visi', value: '' },
                                { label: 'Nuotoliniu būdu', value: 'ONLINE' },
                                { label: 'Gyvai', value: 'ONSITE' }
                            ]}
                            size="small"
                            sx={{ minWidth: 150 }}
                        />
                    </Box>
                </Box>

                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: { xs: 'column', md: 'row' },
                        alignItems: 'flex-start',
                        gap: 4,
                        mt: 4,
                    }}
                >

                    {/* KAIRĖ PUSĖ */}
            <Box sx={{ flex: 2, minWidth: 0 }}>
                {topPicks.length > 0 && (
                    <Box sx={{ mb: 4, mt:4, flex:2}}>
                        <Typography variant="subtitle1" sx={{ fontStyle: 'italic', color: 'gray', }}>
                            🎯 Top užklausos <strong>{query}</strong> rezultatai
                        </Typography>
                        <Box
                            sx={{
                                display: 'flex',
                                overflowX: 'auto',
                                gap: 2,
                                pb: 1,
                                justifyContent: 'center',
                                px: 1,
                                scrollSnapType: 'x mandatory',
                                '&::-webkit-scrollbar': { height: '8px' },
                                '&::-webkit-scrollbar-thumb': {
                                    backgroundColor: '#ff9800',
                                    borderRadius: 4,
                                },
                            }}
                        >
                            {topPicks.map((activity) => (
                                <Box
                                    key={activity.id}
                                    sx={{
                                        minWidth: 300,
                                        flex: '0 0 auto',
                                        scrollSnapAlign: 'start',
                                    }}
                                >
                                    <ActivityCard activity={activity} />
                                </Box>
                            ))}
                        </Box>
                    </Box>
                )}
                    {otherResults.length > 0 && (
                        <Typography variant="subtitle1" sx={{ fontStyle: 'italic', color: 'gray', mb: 2 }}>
                            🧐 Kiti užklausos - <strong>{query}</strong> rezultatai
                        </Typography>
                    )}
                    <Grid
                        container
                        spacing={3}
                        justifyContent="center"
                        sx={{ px: 2 }}
                    >
                        {otherResults.length === 0 ? (
                            <Typography>Nerasta jokių veiklų</Typography>
                        ) : (
                            otherResults.map((activity) => (
                                <Grid item key={activity.id} xs={12} sm={6} md={4} lg={2.4}>
                                    <ActivityCard activity={activity} />
                                </Grid>
                            ))
                        )}
                    </Grid>
                </Box>



                </Box>
            </Box>
        </Container>
    );
};

export default SearchPage;