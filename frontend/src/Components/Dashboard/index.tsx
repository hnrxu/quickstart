import { useEffect, useState } from "react";
import Transactions from "../Transactions";
import SummaryWidget from "../Summary";
import styles from "./index.module.scss";

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

    fetchTransactions();

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

    fetchSummaryData();
    }, []);

    return <div>
        <div className={styles.dashboardLayout}> 
            <Transactions transactions={transactions}/>
            <SummaryWidget summary={summaries} />
        </div>  
    </div>
}

export default Dashboard;