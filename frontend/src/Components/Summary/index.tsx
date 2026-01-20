import Dashboard, { Category, Store } from "../Dashboard";
import styles from "./index.module.scss";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell, RectangleProps } from "recharts";




type SummaryProps = {
    freqCategories: Category[];
    spentCategories: Category[];
    freqStores: Store[];
    spentStores: Store[];
}
const SummaryWidget = ({freqCategories, spentCategories, freqStores, spentStores}: SummaryProps) => {

   
    console.log(freqCategories)
    const getColorFromCss = (cssColor: string) => {

        return getComputedStyle(document.documentElement)
        .getPropertyValue(cssColor)
        .trim();
    }
    
    const barColors = [getColorFromCss("--outline2"),
                        getColorFromCss("--magenta"),
                        getColorFromCss("--pink4"),
                        getColorFromCss("--pink3"),
                        getColorFromCss("--pink2")
                    ];
   

    if(freqCategories.length === 0 || spentCategories.length === 0 ) {return <div> loading...</div>}


    // making highlights/borders on custom bar using shape
    const PixelBar = (props: RectangleProps) => {
        const { x, y, width, height, fill } = props;

        if (
            x == null ||
            y == null ||
            width == null ||
            height == null ||
            !fill
        ) {
            return null;
        }

        const highlightSize = 7;
        const strokeSize = 4;

        return (
            <g>
            {/* main bar */}
            <rect
                x={x+strokeSize}
                y={y-strokeSize}
                width={width}
                height={height}
                fill={fill}
                stroke={getColorFromCss("--outline2")} 
                strokeWidth={highlightSize}
                
                
            />

           
            
            

            <rect
                x={x+strokeSize}
                y={y+height-highlightSize-strokeSize+1}
                width={width}
                height={highlightSize}
                fill={getColorFromCss("--brightpurple")} 
                //opacity={0.85}
                
         
               
            />
            <rect
                x={x+width-highlightSize+strokeSize}
                y={y-strokeSize}
                width={highlightSize}
                height={height}
                fill={getColorFromCss("--brightpurple")} 
                //opacity={0.75}
       
                
            />

            <rect
                x={x+strokeSize}
                y={y-strokeSize}
                width={width}
                height={highlightSize}
                fill={getColorFromCss("--pink4")} 
                
             
               
            />

            <rect
                x={x+strokeSize}
                y={y-strokeSize}
                width={highlightSize}
                height={height}
                fill={getColorFromCss("--pink3")} 
                //opacity={0.85}
                
                
            />

            
            </g>
        );
    };





    return <div className={styles.outerContainer}>
        <div className={styles.summariesContainer}>

            <div className={styles.catfreqChartContainerShadow}>

            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    <b>Most Frequent Expense Categories</b>
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRankShadow}>
                        <div className={styles.catFreqRank}>
                
                            {freqCategories.slice(0,3).map((c, index) => (
                            <div key={index} className={styles.rankBox}>
                                <span className={styles.rankBadge}>{index + 1}</span>
                                <span className={styles.rankLabel}> {c.name}</span>
                            </div>
                            
                            ))}
                        </div>
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={freqCategories.slice(0,5)}>
                            <XAxis 
                                dataKey="name" 
                                interval={0} 
                                style={{
                                    fontFamily: "Pixelify Sans",
                                    fontSize: 15,
                            
                                    //fontWeight: 250
                                    }} 
                                stroke={getColorFromCss("--outline2")} 
                                strokeWidth={3}
                                tick={{ fill: getColorFromCss("--outline2")}} 
                                tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis 
                                stroke={getColorFromCss("--outline2")} 
                                strokeWidth={3} 
                                tick={{ fill: getColorFromCss("--outline2") }} 
                                style={{
                                fontFamily: "Pixelify Sans",
                                fontSize: 15,
                        
                                //fontWeight: 250
                                }} 
                                
                            />
                            
                            <Bar dataKey="num_purchases" fill="#b684d8ff" shape={<PixelBar />}>
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={getColorFromCss("--magenta")}
                                         />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>

            </div>



            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    Most Frequently Visited Stores
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRank}>
            
                        {freqStores.slice(0,3).map((s, index) => (
                        <div key={index} className={styles.rankBox}>
                            <span className={`${styles.rankBadge} ${styles[`rank${index + 1}`]}`}>{index + 1}</span>
                            <span className={styles.rankLabel}>{s.name}</span>
                        </div>
                        
                        ))}
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={freqStores.slice(0,5)}>
                            <XAxis 
                            dataKey="name" 
                            interval={0} 
                            style={{
                                fontFamily: "Inter",
                                //fontWeight: 250
                                }} 
                            stroke="black" 
                            tick={{ fill: 'black'}} 
                            tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis stroke="black" tick={{ fill: 'black' }} />
                            
                            <Bar dataKey="num_purchases" fill="#b684d8ff" >
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={barColors[index % barColors.length]} />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>



            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    Categories Most Spent At
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRank}>
            
                        {spentCategories.slice(0,3).map((c, index) => (
                        <div key={index} className={styles.rankBox}>
                            <span className={`${styles.rankBadge} ${styles[`rank${index + 1}`]}`}>{index + 1}</span>
                            <span className={styles.rankLabel}>{c.name}</span>
                        </div>
                        
                        ))}
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={spentCategories.slice(0,5)}>
                            <XAxis 
                            dataKey="name" 
                            interval={0} 
                            style={{
                                fontFamily: "Inter",
                                //fontWeight: 250
                                }} 
                            stroke="black" 
                            tick={{ fill: 'black'}} 
                            tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis stroke="black" tick={{ fill: 'black' }} />
                            
                            <Bar dataKey="total_spent" fill="#b684d8ff" >
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={barColors[index % barColors.length]} />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>



            <div className={styles.catfreqChartContainer}>
                <div className={styles.title}>
                    Stores Most Spent At
                </div>
                <div className={styles.catfreqChart}>
                    <div className={styles.catFreqRank}>
            
                        {spentStores.slice(0,3).map((s, index) => (
                        <div key={index} className={styles.rankBox}>
                            <span className={`${styles.rankBadge} ${styles[`rank${index + 1}`]}`}>{index + 1}</span>
                            <span className={styles.rankLabel}>{s.name}</span>
                        </div>
                        
                        ))}
                    </div>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart  data={spentStores.slice(0,5)}>
                            <XAxis 
                            dataKey="name" 
                            interval={0} 
                            style={{
                                fontFamily: "Inter",
                                //fontWeight: 250
                                }} 
                            stroke="black" 
                            tick={{ fill: 'black'}} 
                            tickFormatter={(value) => `${value.slice(0, 7)}...`} 
                            ></XAxis>
                            <YAxis stroke="black" tick={{ fill: 'black' }} />
                            
                            <Bar dataKey="total_spent" fill="#b684d8ff" >
                                {
                                    freqCategories.map((bar, index) => (
                                        <Cell key={index} fill={barColors[index % barColors.length]} />
                                        
                                    ))
                                }
                            </Bar> 
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>




        </div> 
        
    </div>
}

export default SummaryWidget;