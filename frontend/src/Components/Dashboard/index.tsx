import { useEffect, useState } from "react";

const Dashboard = () => {

    type Summary = {
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
    };

    const [summaries, setSummaries] = useState<Summary|null>(null);

    useEffect(() => {
        const fetchSummaryData = async () => {
            const response = await fetch("https://quickstart-lwsu.onrender.com/api/summarydata",
                { method: "GET"});
            const summaryData = await response.json();
            console.log(summaryData);
            setSummaries(summaryData);
        }

        fetchSummaryData();
    }, []) /// only call once upon render for now

    {if(!summaries) {return <div> loading...</div>}}
    
    return <div>Viewing Transactions
        {summaries?.most_frequent_category.name}
        {summaries?.most_spent_category.name}
    </div>
}

export default Dashboard;