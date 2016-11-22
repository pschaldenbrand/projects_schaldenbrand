function [bow] = computeBOWRepr(descriptors, means)

bow = double(zeros(size(means,2),1));

for i = 1:1:size(descriptors,2)
    d = descriptors(:,i);
    minDist = 99999999;
    closestClusterInd = -1;
    
    for j = 1:1:size(means,2)
        cluster = means(:,j);
        
        dist = dist2(cluster', d');
        
        if dist < minDist
            minDist = dist;
            closestClusterInd = j;
        end
    end
    bow(closestClusterInd,1) = bow(closestClusterInd,1) + 1;
end
if (sum(bow) > 0 )
    bow = bow ./ double(sum(bow));
end
