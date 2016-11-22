framesdir = 'Homework 5/frames_subset/frames_subset/';
siftdir = 'Homework 5/sift_subset/sift_subset/';

fnames = dir([siftdir '*.mat']);
assert(size(fnames, 1) > 0);
fnames2 = dir([framesdir '*.jpeg']);
assert(size(fnames2, 1) > 0);

%make means
load('centers.mat','means','membership');


%MAKE THE BOW FOR ALL OTHER IMAGES
otherBows = [];
for i = 1:1:300%size(fnames,1)              CHANGE THIS
    disp(i);
    fname = [siftdir '/' fnames(i).name];
    load(fname, 'imname', 'descriptors', 'positions', 'scales', 'orients');
    
    imname = [framesdir '/' imname]; % add the full path
    im = imread(imname);
    
    t = computeBOWRepr(descriptors', means);
    otherBows(:,i) = t;
end

%bow = double(zeros(size(means,2),1));
for i = 1:1:5        
    queryNum = randi(300);
    
    figure;
    fname = [siftdir '/' fnames(queryNum).name];
    load(fname, 'imname', 'descriptors', 'positions', 'scales', 'orients');
    imname = [framesdir '/' imname]; % add the full path
    im = imread(imname);
    
    oninds = selectRegion(im, positions);
    region_descr = [];
    for j = 1:1:size(oninds,1)
        temp = descriptors(j,:);
        region_descr(:,end+1) = temp(:);
    end
     
    bow = double(zeros(size(means,2),1));
    t = computeBOWRepr(region_descr, means);
    bow = bow + t;
    
    %COMPARE NOW TO FIND TOP 3 IMAGES    
    sims = [];
    for j = 1:1:size(otherBows,2)
        otherBow = otherBows(:,j);
        sim = compareSimilarity(bow, otherBow);
        sims(1,j) = sim;
    end
    sims = sims(:);
    topThreeInd = [];
    for j = 1:1:3
        val = max(sims);
        val = val(1,1);
        ind = find(sims == val)
        topThreeInd(end+1) = ind;
        sims(ind) = [];
        for k = 1:1:size(topThreeInd,2)
            if topThreeInd(k) <= ind
                ind = ind + 1;
            end
        end
        
        figure;
        title(strcat('Query #',i,' Match #',j));
        fname = [siftdir '/' fnames(ind).name];
        load(fname, 'imname', 'descriptors', 'positions', 'scales', 'orients');
        imname = [framesdir '/' imname]; % add the full path
        im1 = imread(imname);
        imshow(im1);
    end
    
end







