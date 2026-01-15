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

export type Summary = {
        most_frequent_category: {
            name: string;
            total_spent: number;
            num_purchases: number;
        };
        most_spent_category: {
            name: string;
            total_spent: number;
            num_purchases: number;
        };
    }

const Dashboard = () => {

    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [summaries, setSummaries] = useState<Summary|null>(null);

    

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
        setSummaries(summaryData);
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
        setSummaries(null);

    }


    return <div>
        <div className={styles.dashboardLayout}> 
            <button type="button" onClick={() => removeItem()}></button>
            <div className={styles.balance}>
                hello

            </div>
            <div className={styles.gridContainer}>
                <div> 
                    <Transactions transactions={transactions}/>
                </div>
                <div> 
                    <SummaryWidget summary={summaries} />
                </div>
            </div>
           
        </div>  
    </div>
}

export default Dashboard;