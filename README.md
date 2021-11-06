## Challenge task: 
a REST service that aims to guess the weight (score) of input keyword within Amazon's autosuggest/typeahead index.
provides endpoint estimate to output guessed score: 
```
GET /estimate
URL param: keyword
response:
{
    "keyword": "normalized valid term",
    "score": 1
} 
```
### Scoring model
The task gives just a brief definition that 

>the score should be in the range \[0, 100] and represent the estimated search-volume 
(how often Amazon customers search for that exact keyword).
A score of 0 means that the keyword is practically never searched for,
100 means that this is one of the hottest keywords in all of amazon.com right now.

Given the challenge's author specifics (SEO-like service for Amazon platform) - the scoring model should rather be universal for terms with different traits to provide an ability to compare scores to benefit optimization effort.
The universality of the "score" obliges the scoring model to assume the whole Amazon suggestion index, rather than API output for a specific term alone.
In the same time task restricts using of prefetched data snapshots, so down below there come observations to formulate universal scoring model. 
##### the hottest keywords / 100 points in score
Amazon probably would assume these for the most generic user, suggesting the keywords for the user-input from none to marginal.
the minimal input Amazon API allows is a single character \[a-z0-9\].

**rule #1**: the highest score keywords come in suggested lists for such input.
##### the never searched keywords / 0 points in score
Amazon won't index such. Amazon API doesn't suggest the known keyword for the same exact input; To check if the keyword known to Amazon the API should respond with it.

**rule #2**: if Amazon API can suggest a keyword for the same exact input without the last letter - it should score at least 1, otherwise - 0
##### what's in-between of 1-99?
Amazon index would almost certainly be a balanced trie-like structure with weights (say, number of requests led to actual order per period)
That means competition between suggests closer to the root grows exponentially. This assumption comes with two outcomes:
**rule #3-4**
3. the shorter input prefix required to match a keyword - the bigger should be the score in exponential manner
4. for longer keywords at some point it shouldn't matter whether typing next character will result in suggest match due to insufficient score granularity and marginal score impact.
 
Checking hottest suggestions (entering 1 letter at a turn) - you can observe, that keyword length varies mostly from 3 to 30.
Probably that will depend on the language, goods category, time of the year, etc... but.
**rule #5** let's assume 14-15 chars matching prefix length as threshold for score impact.

### What You have to deliver: How does your algorithm work?
1. Validate/normalize input.
2. Make up to 15 suggest Amazon API requests, starting with a single character, and subsequently adding more from the input keyword (see rule #5)
3. Identify which shortest prefix resulted in match between proposed suggests and input 
4. Calculate score based on shortest prefix length (see rules #3-4) - `SCORE=100*exp(-0.3*IT)`

### What You have to deliver: Do you think the (*hint) that we gave you earlier is correct and if so - why?
At least the scoring model I proposed, fits the hint, as the major score impact comes from the prefix length, and to add granularity with the suggests order - you will have to make it dependent on the prefix length anyway, to avoid calculation anomalies.

### What You have to deliver: How precise do you think your outcome is and why?
It definitely won't allow to reconstruct suggest-index, but it may help with rough estimations. (why? see the assumption on suggest-index data structure)