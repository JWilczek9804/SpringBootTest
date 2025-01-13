import React from 'react';
import {
    Button,
    Wrap,
    WrapItem
} from "@chakra-ui/react";
import SidebarWithHeader from "./shared/SideBar.jsx"
import CardWithImage from "./components/CardWithImage.jsx"

import {getCustomers} from "./services/client.js"
import {useEffect,useState} from "react"

const App = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        setTimeout(()=> {
        getCustomers()
            .then(response => setCustomers(response.data))
            .catch(e => console.log(e))
            .finally(()=> setLoading(false));
        },1000)
    }, []);

    console.log(customers);
    // console.log(data);

    return (
        <SidebarWithHeader>
            {/*<Button variant="solid">Solid</Button>*/}
            <Wrap justify={'center'} spacing={"60px"}>
            {customers.map((customer,index)=> (
                <WrapItem>
                <CardWithImage key={index} customer={customer}/>
                </WrapItem>

            ))}
            </Wrap>
        </SidebarWithHeader>
    )
};

export default App;