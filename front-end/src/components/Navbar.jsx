import React from 'react';
import { Link } from 'react-router-dom';
import { Toolbar, Button, Typography, Box} from "@mui/material";
import { useAuth } from "../context/AuthContext";
import toolbarConfig from "../config/toolbarConfig.jsx";
import ButtonFunky from "./ButtonFunky.jsx";
import SearchBar from "./SearchBar.jsx";

const Navbar = () => {
    const { token, roles, logout } = useAuth();

    const filteredLinks = toolbarConfig.filter(({ roles: allowedRoles }) =>
        !allowedRoles || allowedRoles.some(role => roles.includes(role))
    );

    return (
        <Box sx={{
            width: '100%',
            margin: '0 auto',
        }}>
            <Toolbar
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    position: 'fixed',
                    alignItems: 'center',
                    top: 0,
                    left: 0,
                    right: 0,
                    zIndex: 1000,
                    backgroundColor: 'white',
                    paddingX: '40px',
                    gap: '20px',
                    flexWrap: 'wrap',
                    mt: 1
                }}
            >

                <Button color="inherit" component={Link} to="/main">
                    <Typography sx={{fontSize: '22px', fontWeight: 'bold'}}>
                        Popamokslis
                    </Typography>

                </Button>

                {token && (
                    <>
                        {filteredLinks.map(({path, label}) => (
                            label && (
                                <Button
                                    key={path}
                                    color="inherit"
                                    component={Link}
                                    to={path}
                                    sx={{fontSize: '13px', fontWeight: '400'}}
                                >
                                    {label}
                                </Button>
                            )
                        ))}
                        <Box sx={{
                            flexGrow: 1,
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            marginLeft: 'auto',
                        }}>
                            <SearchBar/>
                        </Box>

                        <ButtonFunky sx={{display: 'flex',}} onClick={() => window.location.href = '/user-profile'}>
                            Profile
                        </ButtonFunky>

                        <ButtonFunky
                            onClick={logout}
                        >
                            Sign Out
                        </ButtonFunky>
                    </>
                )}
            </Toolbar>
        </Box>
    );
};

export default Navbar;