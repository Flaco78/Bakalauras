import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Toolbar, Button, Typography, Box } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import toolbarConfig from '../config/toolbarConfig.jsx';
import ButtonFunky from './ButtonFunky.jsx';
import SearchBarFunky from './SearchBar.jsx';
import { MenuItem, } from '@mui/material';
import axios from 'axios';
import {useChild} from "../context/ChildContext.jsx";
import FunkySelect from "./FunkySelect.jsx";

const Navbar = () => {
    const { token, roles, logout } = useAuth();
    const [searchValue, setSearchValue] = React.useState('');
    const [hidden, setHidden] = useState(false);
    const [lastScrollY, setLastScrollY] = useState(0);
    const [children, setChildren] = useState([]);
    const { selectedChildId, updateSelectedChild } = useChild();

    const filteredLinks = toolbarConfig.filter(({ roles: allowedRoles }) =>
        !allowedRoles || allowedRoles.some(role => roles.includes(role))
    );

    const handleScroll = () => {
        if (window.scrollY > lastScrollY) {
            setHidden(true);
        } else {
            setHidden(false);
        }
        setLastScrollY(window.scrollY);
    };

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, [lastScrollY]);

    useEffect(() => {
        const storedChildId = localStorage.getItem('selectedChildId');
        if (storedChildId) {
            updateSelectedChild(storedChildId);
        }

        // Fetch child profiles if user is logged in
        if (token && roles.includes('USER')) {
            axios.get('/api/auth/user/child-profiles', {
                headers: { Authorization: `Bearer ${token}` },
            }).then(res => {
                setChildren(res.data);
            }).catch(err => {
                console.error("Failed to fetch children:", err);
            });
        }
    }, [token]);

    const handleChildSelect = (e) => {
        const childId = e.target.value;
        updateSelectedChild(childId);
    };

    const handleLogout = () => {
        localStorage.removeItem('selectedChildId');
        logout();
    };

    return (
        <Box sx={{
            width: '100%',
            position: 'relative',
        }}>
            <Toolbar
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    top: hidden ? '-100px' : '0',
                    left: 0,
                    right: 0,
                    zIndex: 10,
                    backgroundColor: 'white',
                    paddingX: '40px',
                    gap: '20px',
                    flexWrap: 'wrap',
                    position: 'fixed',
                    transition: 'top 0.3s ease-in-out',
                }}
            >
                <Button color="inherit" component={Link} to="/main">
                    <Typography sx={{ fontSize: '22px', fontWeight: 'bold' }}>
                        Popamokslis
                    </Typography>
                </Button>

                {token && (
                    <>
                        {filteredLinks.map(({ path, label }) => (
                            label && (
                                <Button
                                    key={path}
                                    color="inherit"
                                    component={Link}
                                    to={path}
                                    sx={{ fontSize: '13px', fontWeight: '400' }}
                                >
                                    {label}
                                </Button>
                            )
                        ))}
                        {roles.includes('USER') && (

                            <FunkySelect
                                value={selectedChildId || ''}
                                onChange={handleChildSelect}
                            >
                                <MenuItem value="" disabled>
                                    Pasirinkite vaiką 👶
                                </MenuItem>
                                {children.map(child => (
                                    <MenuItem key={child.id} value={child.id}>
                                        {child.name}
                                    </MenuItem>
                                ))}
                            </FunkySelect>

                            )}

                        <Box sx={{
                            flexGrow: 1,
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            marginLeft: 'auto',
                        }}>
                            <SearchBarFunky
                                value={searchValue}
                                onChange={(e) => setSearchValue(e.target.value)}
                            />
                        </Box>


                        {selectedChildId && (
                            <ButtonFunky
                                component={Link}
                                to="/favorites"
                            >
                                Mėgstamos veiklos
                            </ButtonFunky>
                        )}

                        {(roles.includes('USER') || roles.includes('PROVIDER')) && (
                        <ButtonFunky sx={{ display: 'flex', }} onClick={() => window.location.href = '/user-profile'}>
                            Profilis
                        </ButtonFunky>
                            )}

                        <ButtonFunky onClick={handleLogout}>
                            Atsijungti
                        </ButtonFunky>
                    </>
                )}
            </Toolbar>
        </Box>
    );
};

export default Navbar;