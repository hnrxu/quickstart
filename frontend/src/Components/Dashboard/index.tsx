import { useContext, useEffect, useState } from "react";
import Transactions from "../Transactions";
import SummaryWidget from "../Summary";
import styles from "./index.module.scss";
import Context from "../../Context";

/// types (may nove later??)
export type Transaction = {
        transaction_id: string;
        name: string;
        amount: number;
        merchant_name?: string | null;
        date: dateArray;
    }
export type dateArray = [number, number, number];


export type Category = {
    name: string;
    num_purchases: number;
    total_spent: number;
    budget?: number | null;
}

export type Store = {
    name: string;
    num_purchases: number;
    total_spent: number;
    budget?: number | null;
    category: Category;
    storetype: string;
    location?: string | null;

}

const Dashboard = () => {

    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [freqCategories, setFreqCategories] = useState<Category[]>([]); 
    const [spentCategories, setSpentCategories] = useState<Category[]>([]); 
     const [freqStores, setFreqStores] = useState<Store[]>([]); 
    const [spentStores, setSpentStores] = useState<Store[]>([]); 

    

    useEffect(() => {
    const fetchTransactions = async () => {
        const response = await fetch(
        "https://quickstart-lwsu.onrender.com/api/transactions",
        { method: "GET" }
        );

        const transactionData = await response.json();
        setTransactions(transactionData.latest_transactions);
       

    };

    

    const fetchSummaryData = async () => {
        const response = await fetch("https://quickstart-lwsu.onrender.com/api/summarydata",
            { method: "GET"});
        if (!response.ok) {
            console.error("summarydata failed", response.status);
            return;
        }
        const summaryData = await response.json();
        setFreqCategories(summaryData.most_frequent_categories);
        //console.log("LOOK HERE", summaryData.most_frequent_categories);
        setSpentCategories(summaryData.most_spent_categories);
        setFreqStores(summaryData.most_frequent_stores);
        setSpentStores(summaryData.most_spent_stores);
    }

    const loadData = async () => { // need this await gguards to make sure transactions finished fetching before calling summary
        await fetchTransactions();
        await fetchSummaryData();
    }

    loadData();
    

    
    }, []);

    const{ dispatch } = useContext(Context); /// allows me to update linksuccess

    // disconnecting item logic to prevent items getting clogged up as rn they are in memory
    const removeItem = async () => {
        const response = await fetch("https://quickstart-lwsu.onrender.com/api/removeitem",
            { method: "POST"}
        )
        if (!response.ok) {
            console.error("remove item failed", response.status);
            alert("Failed to disconnect!");
            return;
        }

        dispatch({
            type: "SET_STATE",
            state: { linkSuccess: false, 
                itemId: null, 
                accessToken: null, 
                isItemAccess: false}
           
        });

        setTransactions([]);
        setFreqCategories([]);
        setSpentCategories([]);

    }


    return <div>
        <div className={styles.dashboardLayout}> 
            <button type="button" onClick={() => removeItem()}>disconnect from bank </button> 
            <div className={styles.balance}>
                hello

            </div>
            <div className={styles.gridContainer}>
                <div> 
                    <Transactions transactions={transactions}/>
                </div>
                <div> 
                    <SummaryWidget freqCategories={freqCategories}
                                spentCategories={spentCategories}
                                freqStores={freqStores}
                                spentStores={spentStores} />
                </div>
            </div>
           
        </div>  
    </div>
}

export default Dashboard;