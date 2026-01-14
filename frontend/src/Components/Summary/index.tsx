import Dashboard, { Summary } from "../Dashboard";

type SummaryProps = {
    summary: Summary|null;
}
const SummaryWidget = ({summary}: SummaryProps) => {
    return <div>
        Category most frequently purchased from: {summary?.most_frequent_category.name} <br /> with {summary?.most_frequent_category.num_purchases} purchases;
        Category most spent at: {summary?.most_spent_category.name} <br /> with ${summary?.most_spent_category.total_spent} spent;
    </div>
}

export default SummaryWidget;