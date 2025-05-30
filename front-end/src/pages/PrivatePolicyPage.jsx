import React from 'react';
import { Container, Typography, Box, List, ListItem } from '@mui/material';

const PrivatePolicyPage = () => {
    return (
        <Container maxWidth="md" sx={{ py: 5 }}>
            <Typography variant="h4" gutterBottom>
                Privatumo politika
            </Typography>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Paskutinį kartą atnaujinta: 2025 m. gegužės 4 d.
            </Typography>

            <Box sx={{ mt: 3, textAlign: 'center' }}>
                <Typography variant="h6">1. Įžanga</Typography>
                <Typography variant="body1" paragraph>
                    Ši privatumo politika aprašo mūsų politiką ir procedūras dėl Jūsų informacijos rinkimo, naudojimo
                    ir atskleidimo, kai naudojatės Paslauga, ir paaiškina Jūsų privatumo teises bei tai, kaip įstatymai Jus apsaugo.
                </Typography>
                <Typography variant="body1" paragraph>
                    Mes naudojame Jūsų asmens duomenis tam, kad galėtume teikti ir tobulinti Paslaugą. Naudodamiesi Paslauga, Jūs sutinkate su informacijos rinkimu ir naudojimu pagal šią Privatumo politiką.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>Terminai ir sąvokos</Typography>
                <Typography variant="body1" paragraph>
                    <strong>Paskyra</strong> – unikali paskyra, sukurta Jums naudotis mūsų Paslauga arba jos dalimis.
                    <br />
                    <strong>Susijusi įmonė</strong> – subjektas, kuris kontroliuoja, yra kontroliuojamas arba veikia kartu su Šalimi.
                    <br />
                    <strong>Įmonė</strong> (šiame dokumente vadinama „Popamokslis“, „mes“, „mūsų“, „mus“) – tai „Popamokslis“ projektas.
                    <br />
                    <strong>Slapukai</strong> – maži failai, saugomi Jūsų įrenginyje, kuriuose yra naršymo istorija.
                    <br />
                    <strong>Šalis</strong> – Lietuva.
                    <br />
                    <strong>Įrenginys</strong> – bet koks įrenginys, kuriuo galima naudotis Paslauga (kompiuteris, telefonas ir pan.).
                    <br />
                    <strong>Asmens duomenys</strong> – bet kokia informacija, susijusi su identifikuotu asmeniu.
                    <br />
                    <strong>Paslauga</strong> – svetainė Popamokslis, pasiekiama adresu http://localhost:5173
                    <br />
                    <strong>Paslaugų teikėjas</strong> – trečioji šalis, kuri tvarko duomenis mūsų vardu.
                    <br />
                    <strong>Naudojimo duomenys</strong> – automatiškai renkami duomenys, pvz., IP adresas, naršyklės tipas, lankytų puslapių trukmė ir pan.
                    <br />
                    <strong>Jūs</strong> – asmuo, besinaudojantis Paslauga.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>2. Asmens duomenų rinkimas ir naudojimas</Typography>
                <Typography variant="body1" paragraph>
                    <strong>Renkami duomenų tipai</strong>
                </Typography>
                <List>
                    <ListItem>El. pašto adresas</ListItem>
                    <ListItem>Vardas ir pavardė</ListItem>
                    <ListItem>Telefono numeris</ListItem>
                    <ListItem>Adresas, miestas, pašto kodas</ListItem>
                    <ListItem>Naudojimo duomenys</ListItem>
                </List>
                <Typography variant="body1" paragraph>
                    <strong>Naudojimo duomenys:</strong> Tai apima Jūsų įrenginio IP adresą, naršyklės tipą, peržiūrėtus puslapius, apsilankymo laiką, ir kitą diagnostinę informaciją.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>3. Slapukai ir sekimo technologijos</Typography>
                <Typography variant="body1" paragraph>
                    Naudojame slapukus (angl. cookies), žymas (beacons) ir scenarijus (scripts) siekiant stebėti Paslaugos naudojimą. Naudojami slapukai:
                </Typography>
                <List>
                    <ListItem>Būtini slapukai – būtini Paslaugos funkcionalumui.</ListItem>
                    <ListItem>Sutikimo slapukai – fiksuoja, ar sutikote su slapukų naudojimu.</ListItem>
                    <ListItem>Funkciniai slapukai – prisimena Jūsų pasirinkimus (pvz., kalbos nustatymai).</ListItem>
                </List>

                <Typography variant="h6" sx={{ mt: 3 }}>4. Jūsų asmens duomenų naudojimas</Typography>
                <Typography variant="body1" paragraph>
                    Jūsų asmens duomenis naudojame:
                </Typography>
                <List>
                    <ListItem>Norėdami teikti ir palaikyti Paslaugą</ListItem>
                    <ListItem>Norėdami valdyti Jūsų paskyrą</ListItem>
                    <ListItem>Sutarties vykdymui</ListItem>
                    <ListItem>Norėdami susisiekti su Jumis (el. paštu, telefonu ar kt.)</ListItem>
                    <ListItem>Norėdami pateikti naujienas, pasiūlymus ar informaciją apie panašias paslaugas</ListItem>
                    <ListItem>Norėdami vykdyti analizę, verslo perdavimus ar rinkodaros vertinimus</ListItem>
                </List>

                <Typography variant="h6" sx={{ mt: 3 }}>5. Jūsų informacijos saugojimas ir perdavimas</Typography>
                <Typography variant="body1" paragraph>
                    Asmens duomenys saugomi tik tiek, kiek reikia pagal teisės aktus arba mūsų veiklos poreikius. Jie gali būti perduoti į kitus regionus ar šalis, tačiau mes užtikrinsime jų saugumą.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>6. Jūsų teisės</Typography>
                <Typography variant="body1" paragraph>
                    Turite teisę:
                </Typography>
                <List>
                    <ListItem>Prašyti ištrinti Jūsų duomenis</ListItem>
                    <ListItem>Taisyti, atnaujinti arba pašalinti savo informaciją prisijungę prie paskyros</ListItem>
                    <ListItem>Susisiekti su mumis dėl bet kokių klausimų</ListItem>
                </List>

                <Typography variant="h6" sx={{ mt: 3 }}>7. Duomenų atskleidimas</Typography>
                <Typography variant="body1" paragraph>
                    Jūsų duomenys gali būti atskleisti:
                </Typography>
                <List>
                    <ListItem>Verslo sandorių atveju</ListItem>
                    <ListItem>Teisėsaugai, jei to reikalauja įstatymai</ListItem>
                    <ListItem>Siekiant apginti mūsų teises ar apsaugoti kitus</ListItem>
                </List>

                <Typography variant="h6" sx={{ mt: 3 }}>8. Duomenų saugumas</Typography>
                <Typography variant="body1" paragraph>
                    Mes stengiamės apsaugoti Jūsų duomenis, bet negalime garantuoti absoliutaus saugumo.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>9. Vaikų privatumas</Typography>
                <Typography variant="body1" paragraph>
                    Paslauga nėra skirta jaunesniems nei 13 metų vaikams. Jei sužinotume, kad tokie duomenys buvo surinkti, juos ištrinsime.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>10. Nuorodos į kitas svetaines</Typography>
                <Typography variant="body1" paragraph>
                    Mūsų svetainėje gali būti nuorodų į trečiųjų šalių puslapius – už jų privatumo politiką mes neatsakome.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>11. Privatumo politikos pakeitimai</Typography>
                <Typography variant="body1" paragraph>
                    Mes galime atnaujinti šią politiką. Apie pakeitimus informuosime el. paštu arba paskelbsime svetainėje.
                </Typography>

                <Typography variant="h6" sx={{ mt: 3 }}>12. Kontaktai</Typography>
                <Typography variant="body1" paragraph>
                    Jeigu turite klausimų, kreipkitės el. paštu: <strong>dominykas.slusnys01@gmail.com</strong>
                </Typography>
            </Box>
        </Container>
    );
};

export default PrivatePolicyPage;